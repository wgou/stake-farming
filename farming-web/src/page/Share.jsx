import React from "react";

export default function Share() {
  return (
    <div className="share-page">
      <div className="share-header">
        <h1 className="share-title">Earn While You Share</h1>
        <p className="share-subtitle">
          Share Your Referral Link With Friends And<br />
          Earn 38% Of Theirrewards
        </p>
        <div className="reward-rate">
          <span className="rate-number">30%</span>
          <span className="rate-label">Reward Rate</span>
        </div>
      </div>

      <div className="invitation-section">
        <div className="invitation-label">YOUR INVITATION LINK</div>
        <div className="invitation-input-wrapper">
          <input 
            type="text" 
            className="invitation-input" 
            value="Connect Your Wallet To Get Your Referral Link"
            readOnly
          />
          <button className="copy-btn">COPY</button>
        </div>
      </div>

      <div className="steps-section">
        <div className="step-item">
          <div className="step-icon step-icon-1">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <div className="step-content">
            <div className="step-number">STEP 01</div>
            <div className="step-text">
              Share Your Unique Referral Link<br />
              With Friends
            </div>
          </div>
        </div>

        <div className="step-item">
          <div className="step-icon step-icon-2">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M12.5 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0zM20 8v6M23 11h-6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <div className="step-content">
            <div className="step-number">STEP 02</div>
            <div className="step-text">
              Friends Join Using Your Link And<br />
              Start Farming
            </div>
          </div>
        </div>

        <div className="step-item">
          <div className="step-icon step-icon-3">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
              <path d="M12 6v6l4 2" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          </div>
          <div className="step-content">
            <div className="step-number">STEP 03</div>
            <div className="step-text">
              Earn 30% Of Their Rewards<br />
              Automaticall
            </div>
          </div>
        </div>
      </div>

      <div className="referral-rewards-section">
        <div className="rewards-header">REFERRRAL REWARDS</div>
        <div className="rewards-table">
          <div className="rewards-table-header">
            <span>DATE(UTC)</span>
            <span>ETH REMARK</span>
          </div>
          <div className="rewards-table-row">
            <span>2025/08/10</span>
            <span>100</span>
          </div>
          <div className="rewards-table-row">
            <span>2025/08/10</span>
            <span>100</span>
          </div>
          <div className="rewards-table-row">
            <span>2025/08/10</span>
            <span>100</span>
          </div>
        </div>
      </div>
    </div>
  );
}

