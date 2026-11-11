UPDATE tenant_settings
SET billing = billing || '{"taxEnabled": false}'::jsonb
WHERE billing ->> 'taxEnabled' IS NULL;
