-- 事件存储表
CREATE TABLE IF NOT EXISTS event_store (
    id BIGSERIAL PRIMARY KEY,
    aggregate_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_data TEXT NOT NULL,
    version BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (aggregate_id, version)
);

-- 账户表
CREATE TABLE IF NOT EXISTS account (
    account_id VARCHAR(36) PRIMARY KEY,
    owner_id VARCHAR(36) NOT NULL,
    asset_type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    available_balance DECIMAL(20,4) NOT NULL,
    frozen_balance DECIMAL(20,4) NOT NULL,
    balance_limit DECIMAL(20,4),
    version BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_event_store_aggregate_id ON event_store(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_account_owner_id ON account(owner_id);
CREATE INDEX IF NOT EXISTS idx_account_status ON account(status);
