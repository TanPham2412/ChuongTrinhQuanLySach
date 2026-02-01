-- Fix: Allow NULL password for OAuth2 users
-- OAuth2 users authenticate through Google and don't have passwords

ALTER TABLE users MODIFY COLUMN password VARCHAR(255) NULL;
