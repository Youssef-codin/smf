# SMF Codebase Notes

## Device Model Concepts

There are three distinct device-related entities. They are easy to confuse.

### SmfDevice
A device that has been **manufactured and verified by SMF**. Seeded manually by a developer to establish that a physical device is genuine. Holds the MAC address, label, and encrypted secret. Has no owner — it represents the hardware, not an assignment.

### Device
Represents a **user's device** — it links an `owner` (User) to a physical MAC address. An unregistered Device just has an owner and MAC; it is not yet verified as SMF hardware. A registered Device has `isRegistered = true` and is connected to an SmfDevice via `RegisteredDevice`.

Think of it as the user-facing side of the pairing.

### RegisteredDevice
The **join table** between `SmfDevice` and `Device`. A row here means a specific SMF-verified device has been paired with a specific user's Device record. Both sides must exist before this record can be created.
