CREATE TABLE trades (
    id UUID PRIMARY KEY,
    external_reference VARCHAR(255) NOT NULL UNIQUE,
    currency_pair VARCHAR(7) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version INTEGER NOT NULL
);

CREATE INDEX idx_trades_status ON trades(status);