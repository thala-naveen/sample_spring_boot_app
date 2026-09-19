-- Flyway Migration V2: Fix avg_monthly_balance_6m column name if table was created by legacy hibernate auto-ddl

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'accounts' AND column_name = 'avg_monthly_balance6m'
    ) THEN
        ALTER TABLE accounts RENAME COLUMN avg_monthly_balance6m TO avg_monthly_balance_6m;
    END IF;
END $$;
