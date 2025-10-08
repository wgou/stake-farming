import React from "react";

export default function Account() {
  return (
    <div className="account-page">
      <div className="my-wallet-section">
        <div className="section-title">MY WALLET</div>
        <div className="wallet-card">
          <div className="wallet-balances">
            <div className="balance-item">
              <div className="balance-label">ETH</div>
              <div className="balance-value">1,288</div>
              <div className="balance-text">BALANCE</div>
            </div>
            <div className="balance-item">
              <div className="balance-label">USDC</div>
              <div className="balance-value">99</div>
              <div className="balance-text">BALANCE</div>
            </div>
          </div>
          <div className="wallet-icon">
            <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' fill='%23a78bfa'/%3E%3C/svg%3E" alt="wallet" />
          </div>
        </div>
      </div>

      <div className="assets-section">
        <div className="section-title">ASSETS</div>
        
        <div className="asset-card">
          <div className="asset-icon asset-icon-eth">
            <div className="icon-circle"></div>
          </div>
          <div className="asset-name">ETH REVEUE</div>
          <div className="asset-value">0</div>
        </div>

        <div className="asset-card">
          <div className="asset-icon asset-icon-exchange">
            <div className="icon-circle"></div>
          </div>
          <div className="asset-name">EXCHANGEABLE</div>
          <div className="asset-value">0</div>
        </div>

        <div className="asset-card">
          <div className="asset-icon asset-icon-balance">
            <div className="icon-circle"></div>
          </div>
          <div className="asset-name">ACOUNT BALANCE</div>
          <div className="asset-value">0</div>
        </div>
      </div>

      <div className="farming-rewards-section">
        <div className="rewards-title">FARMING REWARDS</div>
        <div className="rewards-table">
          <div className="rewards-header">
            <span>DATE</span>
            <span>REWARD</span>
          </div>
          <div className="rewards-row">
            <span>2025/08/10</span>
            <span>999,999</span>
          </div>
          <div className="rewards-row">
            <span>2025/08/10</span>
            <span>—</span>
          </div>
        </div>
      </div>
    </div>
  );
}

