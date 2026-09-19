-- Sentinel AML Flyway Migration Script: V1__init_schema.sql

CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(64) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    gender VARCHAR(10),
    date_of_birth DATE,
    age INT,
    email VARCHAR(150),
    phone_number VARCHAR(50),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(50),
    postal_code VARCHAR(20),
    occupation VARCHAR(100),
    annual_income NUMERIC(15, 2),
    marital_status VARCHAR(50),
    education_level VARCHAR(100),
    employment_status VARCHAR(50),
    customer_since DATE,
    customer_segment VARCHAR(50),
    kyc_status VARCHAR(50),
    risk_rating VARCHAR(20),
    is_politically_exposed INT,
    preferred_channel VARCHAR(50),
    email_verified VARCHAR(5),
    phone_verified VARCHAR(5),
    num_complaints_last_year INT
);

CREATE TABLE IF NOT EXISTS accounts (
    account_id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64) REFERENCES customers(customer_id) ON DELETE SET NULL,
    account_type VARCHAR(50),
    account_status VARCHAR(50),
    currency VARCHAR(10),
    open_date DATE,
    close_date DATE,
    branch_code VARCHAR(50),
    branch_city VARCHAR(100),
    current_balance NUMERIC(15, 2),
    avg_monthly_balance_6m NUMERIC(15, 2),
    credit_limit NUMERIC(15, 2),
    credit_utilization_pct NUMERIC(5, 2),
    overdraft_enabled VARCHAR(5),
    card_type VARCHAR(50),
    is_joint_account INT,
    num_linked_devices INT,
    mobile_banking_enrolled VARCHAR(5),
    last_login_date DATE,
    avg_monthly_txn_count INT,
    account_tier VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(64) PRIMARY KEY,
    account_id VARCHAR(64),
    customer_id VARCHAR(64),
    amount NUMERIC(15, 2),
    currency VARCHAR(10),
    normalized_amount NUMERIC(15, 2),
    transaction_type VARCHAR(50),
    timestamp TIMESTAMP,
    counterparty_account_id VARCHAR(64),
    counterparty_country VARCHAR(50),
    channel VARCHAR(50),
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS rule_configs (
    rule_name VARCHAR(100) PRIMARY KEY,
    description VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,
    threshold_amount NUMERIC(15, 2),
    time_window_hours INT,
    risk_weight INT,
    parameters_json TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alerts (
    alert_id VARCHAR(64) PRIMARY KEY,
    customer_id VARCHAR(64),
    account_id VARCHAR(64),
    risk_score INT,
    status VARCHAR(50) DEFAULT 'NEW',
    triggered_rule VARCHAR(255),
    explanation TEXT,
    evidence_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    disposition_reason TEXT,
    analyst_id VARCHAR(100)
);

-- Seed Default AML Detection Rules
INSERT INTO rule_configs (rule_name, description, enabled, threshold_amount, time_window_hours, risk_weight, parameters_json, updated_at)
VALUES 
('LARGE_TRANSACTION', 'CTR Threshold: Flag single transaction >= $10,000', TRUE, 10000.00, 0, 40, '{"currency":"USD"}', CURRENT_TIMESTAMP),
('STRUCTURING', 'Smurfing: 3+ transactions between $9,000-$9,999 in 24h', TRUE, 9000.00, 24, 50, '{"maxThreshold":9999.99, "minCount":3}', CURRENT_TIMESTAMP),
('RAPID_FUND_MOVEMENT', 'Layering: >=80% deposited funds transferred out within 48h', TRUE, 0.80, 48, 45, '{"percentageOut":80}', CURRENT_TIMESTAMP),
('HIGH_RISK_JURISDICTION', 'Sanctions: Transfer involving high-risk/sanctioned country', TRUE, 0.00, 0, 60, '{"highRiskCountries":["IR","KP","SY","RU","AF","BY","MM"]}', CURRENT_TIMESTAMP),
('BEHAVIORAL_DEVIATION', 'Volume Spike: Transaction exceeds 3x 90-day daily average', TRUE, 3.00, 2160, 35, '{"multiplier":3.0}', CURRENT_TIMESTAMP)
ON CONFLICT (rule_name) DO NOTHING;
