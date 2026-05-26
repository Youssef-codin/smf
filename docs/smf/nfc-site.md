# NFC Worker Medical Card — Google Site Integration

## Overview

NFC tags are attached to worker ID cards. When a medic or first-responder scans the tag with any NFC-capable phone, the browser opens a URL containing the worker's UUID. The page fetches medical and emergency contact data directly from Supabase using a **publishable (anon) key** and displays it clearly.

Spring Boot is **not** involved in this read path — the Google Site calls Supabase directly.

```
NFC tag  →  browser opens URL  →  Google Site page
                                      │
                                      └─ POST Supabase RPC get_worker_medical
                                             (publishable key, no auth required)
                                             │
                                             └─ returns medical/emergency fields only
```

---

## NFC Tag URL Format

Program each tag with:

```
https://<your-google-site-url>/worker?id=<worker-uuid>
```

Example:
```
https://sites.google.com/view/smf-workers/worker?id=a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

The UUID comes from the `id` field returned when the worker is created via `POST /api/v1/workers`.

---

## Supabase RPC

The page calls the `get_worker_medical` RPC which is restricted to return **medical and emergency fields only**. The anon key has no direct `SELECT` access to the `workers` table — it can only call this function.

### Request

```
POST https://bsowsdicxlvfvwwsabhz.supabase.co/rest/v1/rpc/get_worker_medical
Content-Type: application/json
apikey: <SUPABASE_PUBLISHABLE_KEY>

{
  "worker_uuid": "<UUID from URL>"
}
```

### Response — worker found

```json
[
  {
    "full_name_ar": "عمر حسن الرشيدي",
    "full_name_en": "Omar Hassan Al-Rashidi",
    "medical_condition_ar": "داء السكري من النوع الثاني",
    "medical_condition_en": "Type 2 Diabetes",
    "clinical_notes_ar": "يحتاج إلى مراقبة الجلوكوز بانتظام وتناول الأدوية عن طريق الفم.",
    "clinical_notes_en": "Requires regular glucose monitoring and oral medications.",
    "emergency_contact_name": "Sarah Omar",
    "emergency_contact_relation": "Wife",
    "emergency_phone": "0509876543"
  }
]
```

### Response — worker not found

```json
[]
```

---

## Google Site Page Script

Add a Google Sites **Embed** block and paste the following HTML. Replace `YOUR_PUBLISHABLE_KEY` with the Supabase publishable (anon) key.

```html
<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Worker Medical Card</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: system-ui, sans-serif;
      background: #f5f5f5;
      padding: 20px;
      min-height: 100vh;
    }
    .card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      max-width: 480px;
      margin: 0 auto;
      box-shadow: 0 2px 12px rgba(0,0,0,0.1);
    }
    .badge {
      display: inline-block;
      background: #d32f2f;
      color: white;
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 1px;
      padding: 4px 10px;
      border-radius: 4px;
      margin-bottom: 16px;
      text-transform: uppercase;
    }
    h1 { font-size: 22px; color: #111; margin-bottom: 4px; }
    .name-ar { font-size: 16px; color: #555; margin-bottom: 20px; direction: rtl; }
    .section { margin-bottom: 20px; }
    .section-title {
      font-size: 11px;
      font-weight: 700;
      letter-spacing: 1px;
      text-transform: uppercase;
      color: #888;
      margin-bottom: 8px;
      border-bottom: 1px solid #eee;
      padding-bottom: 4px;
    }
    .field { margin-bottom: 8px; }
    .field-label { font-size: 12px; color: #888; margin-bottom: 2px; }
    .field-value { font-size: 15px; color: #111; }
    .field-value.ar { direction: rtl; font-size: 14px; color: #444; }
    .no-condition { color: #2e7d32; font-weight: 600; }
    .emergency { background: #fff3e0; border-radius: 8px; padding: 16px; }
    .emergency .section-title { color: #e65100; }
    .phone-link { color: #1565c0; font-weight: 600; text-decoration: none; font-size: 18px; }
    .error { color: #c62828; text-align: center; padding: 40px 20px; }
    .loading { text-align: center; color: #888; padding: 40px; }
  </style>
</head>
<body>
  <div id="root"><p class="loading">Loading…</p></div>

  <script>
    const SUPABASE_URL = 'https://bsowsdicxlvfvwwsabhz.supabase.co';
    const SUPABASE_ANON_KEY = 'YOUR_PUBLISHABLE_KEY';

    function getParam(name) {
      return new URLSearchParams(window.location.search).get(name);
    }

    function field(label, value, ar) {
      if (!value) return '';
      return `
        <div class="field">
          <div class="field-label">${label}</div>
          <div class="field-value${ar ? ' ar' : ''}">${value}</div>
        </div>`;
    }

    async function load() {
      const id = getParam('id');
      if (!id) {
        document.getElementById('root').innerHTML =
          '<p class="error">No worker ID in URL.</p>';
        return;
      }

      const res = await fetch(`${SUPABASE_URL}/rest/v1/rpc/get_worker_medical`, {
        method: 'POST',
        headers: {
          'apikey': SUPABASE_ANON_KEY,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ worker_uuid: id })
      });

      if (!res.ok) {
        document.getElementById('root').innerHTML =
          '<p class="error">Failed to load worker data. Please try again.</p>';
        return;
      }

      const data = await res.json();
      if (!data || data.length === 0) {
        document.getElementById('root').innerHTML =
          '<p class="error">Worker not found.</p>';
        return;
      }

      const w = data[0];
      const hasMedical = w.medical_condition_en || w.medical_condition_ar;

      document.getElementById('root').innerHTML = `
        <div class="card">
          <div class="badge">Medical Card</div>
          <h1>${w.full_name_en || '—'}</h1>
          ${w.full_name_ar ? `<div class="name-ar">${w.full_name_ar}</div>` : ''}

          <div class="section">
            <div class="section-title">Medical Information</div>
            ${hasMedical
              ? field('Condition', w.medical_condition_en) +
                field('الحالة', w.medical_condition_ar, true) +
                field('Clinical Notes', w.clinical_notes_en) +
                field('ملاحظات سريرية', w.clinical_notes_ar, true)
              : '<div class="field-value no-condition">No known medical conditions</div>'
            }
          </div>

          <div class="section emergency">
            <div class="section-title">Emergency Contact</div>
            ${field('Name', w.emergency_contact_name)}
            ${field('Relation', w.emergency_contact_relation)}
            ${w.emergency_phone
              ? `<div class="field">
                   <div class="field-label">Phone</div>
                   <a class="phone-link" href="tel:${w.emergency_phone}">${w.emergency_phone}</a>
                 </div>`
              : ''
            }
          </div>
        </div>`;
    }

    load();
  </script>
</body>
</html>
```

---

## Security Notes

| Key | Used by | Access |
|---|---|---|
| Publishable (anon) key | Google Site page | `get_worker_medical` RPC only — no direct table access |
| Secret (service role) key | Spring Boot backend | Full table access, bypasses RLS |

- The `get_worker_medical` function returns **only** the fields listed in the Response section above — no addresses, employment details, or internal IDs are exposed.
- Rotate the publishable key in Supabase dashboard → Settings → API if it is ever compromised. Update the key in the Google Site embed afterward.
- The RPC enforces a `LIMIT 1` internally — scanning a tag can never return bulk data.

---

## Adding a New Worker (Full Flow)

1. Create the worker via the Flutter app or directly:
   ```
   POST /api/v1/workers
   Authorization: Bearer <admin-jwt>
   Content-Type: application/json

   { "full_name_en": "...", ... }
   ```
2. Copy the `id` UUID from the response.
3. Program an NFC tag with `https://<site-url>/worker?id=<uuid>`.
4. Attach the tag to the worker's ID card.
