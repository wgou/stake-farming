import React from "react";
import swapIcon from '../assets/icons/swap.png';
import withdrawIcon from '../assets/icons/withdraw.png';

export default function Transaction() {
  const [activeTab, setActiveTab] = React.useState("swap");
  
  return (
    <div className="transaction-page">
      <div className="swap-toggle">
        <button 
          className={`swap-tab ${activeTab === "swap" ? "swap-tab--active" : ""}`}
          onClick={() => setActiveTab("swap")}
        >
          <img src={swapIcon} alt="swap" className="tab-icon" />
          SWAP
        </button>
        <button 
          className={`swap-tab ${activeTab === "withdraw" ? "swap-tab--active" : ""}`}
          onClick={() => setActiveTab("withdraw")}
        >
          <img src={withdrawIcon} alt="withdraw" className="tab-icon" />
          WITHDRAW
        </button>
      </div>

      {activeTab === "swap" ? (
        <div className="trade-card trade-card--white">
          <div className="trade-title">ETH TO USDC</div>
          
          <div className="pay-section">
            <div className="section-header">
              <div className="section-label">YOU PAY</div>
              <div className="section-balance">ETH BALANCE: 999,999,999</div>
            </div>
            <div className="input-box">
              <div className="token-icon token-icon-eth" />
              <div className="token-name">ETH :</div>
              <div className="input-value">100</div>
              <div className="btn-max">MAX</div>
            </div>
          </div>

          <div className="swap-icon-wrapper">
            <div className="swap-icon">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M10 4L10 16M10 4L7 7M10 4L13 7M10 16L7 13M10 16L13 13" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
            </div>
          </div>

          <div className="get-section">
            <div className="section-header">
              <div className="section-label">YOU GET</div>
            </div>
            <div className="input-box input-box--readonly">
              <div className="token-icon token-icon-usdc" />
              <div className="token-name">USDC:</div>
              <div className="input-value">1</div>
            </div>
          </div>

          
          <div className="swap-button-inline">SWAP</div>
        </div>
      ) : (
        <div className="withdraw-card">
          <div className="withdraw-amount-display">
            <div className="amount-large">1,299,999.00</div>
            <div className="amount-label">AVAILABLE AMOUNT</div>
          </div>
          
          <div className="withdraw-input-wrapper">
            <div className="input-box">
              <div className="token-name">USDC:</div>
              <div className="input-value">100</div>
              <div className="btn-max">MAX</div>
            </div>
          </div>

          <div className="swap-button-inline">WITHDRAW</div>
        </div>
      )}

      <div className="history-card">
        <div className="history-title">{activeTab === "swap" ? "SWAP HISTORY" : "WITHDRAW HISTORY"}</div>
        <div className="history-header">
          <span>DATE(UTC)</span>
          <span>{activeTab === "swap" ? "FROM ETH" : "USDC"}</span>
          <span>{activeTab === "swap" ? "TO USDC" : "STATUS"}</span>
          {activeTab === "withdraw" && <span>REMARK</span>}
        </div>
        {activeTab === "swap" ? (
          <>
            <div className="history-row">
              <span>2025/08/10</span>
              <span>999,999</span>
              <span>100</span>
            </div>
            <div className="history-row">
              <span>2025/08/10</span>
              <span>999,999</span>
              <span>—</span>
            </div>
          </>
        ) : (
          <>
            <div className="history-row">
              <span>2025/08/10</span>
              <span>999,999</span>
              <span>1</span>
              <span>/</span>
            </div>
            <div className="history-row">
              <span>2025/08/10</span>
              <span>999,999</span>
              <span>333</span>
              <span>YES</span>
            </div>
            <div className="history-row">
              <span>2025/08/10</span>
              <span>999,999</span>
              <span>999,999</span>
              <span>100</span>
            </div>
          </>
        )}
      </div>
    </div>
  );
}


