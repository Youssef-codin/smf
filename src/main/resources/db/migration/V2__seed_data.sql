

INSERT INTO roles (role_name) VALUES ('ADMIN'), ('USER');



INSERT INTO users (id, username, email, password)
VALUES
  (UNHEX(REPLACE(UUID(),'-','')), 'admin', 'admin@smf.com', '<BCRYPT_HASH_FOR_admin>'),
  (UNHEX(REPLACE(UUID(),'-','')), 'user',  'user@smf.com',  '<BCRYPT_HASH_FOR_user>');


INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE (u.email = 'admin@smf.com' AND r.role_name = 'ADMIN')
   OR (u.email = 'user@smf.com'  AND r.role_name = 'USER');
