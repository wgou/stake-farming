import React from "react";
import "./index.css";
import Transaction from "./Transaction";
import Share from "./Share";
import Account from "./Account";

export default function Main() {
  const [activeTab, setActiveTab] = React.useState("rewards");
  const [activeBottom, setActiveBottom] = React.useState("home");
  const [chatOpen, setChatOpen] = React.useState(false);
  return (
    <div className="main-container">
      <div className="flex-row-cc">
        <div className="image" />
        <div className="rectangle">
          <span className="connect">CONNECT</span>
        </div>
        <span className="farmville"> Farmville</span>
      </div>
      {activeBottom === "home" && (
        <>
      <span className="liquidity-farming">LIOUIDITY FARMING</span>
      <div className="flex-row-f">
        <div className="rectangle-1" />
        <span className="eth-reward">
          ETH
          <br />
          reward
        </span>
        <span className="dot">800.0000</span>
      </div>
      <div className="flex-row-ce">
        <div className="rectangle-2" />
        <div className="mask-group">
          <span className="participants">PARTICIPANTS</span>
          <span className="empty">615193</span>
          <div className="ellipse" />
          <div className="frame" />
        </div>
        <div className="rectangle-3">
          <div className="mask-group-4">
            <div className="flex-row-dfa">
              <div className="frame-5" />
              <div className="ellipse-6" />
            </div>
            <span className="nooes">Nooes</span>
            <span className="text-156">156</span>
          </div>
        </div>
        <div className="rectangle-7">
          <div className="mask-group-8">
            <span className="usdc-verrified">USDC VERRIfied</span>
            <span className="text-854-778m">854,778M</span>
            <div className="ellipse-9" />
            <div className="frame-a" />
          </div>
        </div>
      </div>
      <div className="flex-row-eab">
        <div className="rectangle-b" />
        <div className="rectangle-c" />
        <span className="partnerrs">PARTNERRS</span>
        <div className="vector" />
        <div className="group">
          <div className="image-d" />
        </div>
        <div className="group-e">
          <div className="image-f" />
        </div>
        <div className="group-10">
          <div className="image-11" />
        </div>
        <div className="group-12">
          <div className="image-13" />
        </div>
        <span className="coin-market-cap">CoinMarketCap</span>
        <span className="coin-gecko">CoinGecko</span>
        <span className="trust-wallet">TrustWallet</span>
        <span className="crypto-com">Crypto.com</span>
      </div>
        </>
      )}
      {activeBottom === "home" ? (
        <>
          <span className="farm-eth-liquidity">
            Farm daily interest on UsDc by providing liquidity for ETH farming pools
          </span>
          <div className="rectangle-14">
            <span className="connect-farmville"> CONNECT Farmville</span>
          </div>
          <div className="flex-row-da">
            <div className="rectangle-15" />
            <span
              className={`farming-rewards ${
                activeTab === "rewards" ? "tab-active" : "tab-inactive"
              }`}
              onClick={() => setActiveTab("rewards")}
            >
              Farming rewards
            </span>
            <span
              className={`faq ${
                activeTab === "faq" ? "tab-active" : "tab-inactive"
              }`}
              onClick={() => setActiveTab("faq")}
            >
              FAQ
            </span>
            <div className="vector-16" />
          </div>

          {activeTab === "rewards" ? (
            <>
              <div className="flex-row-ff">
                <div className="rectangle-17" />
                <span className="farmville-address">FARMVILLE ADDRESS</span>
                <span className="eth-reward-18">ETH REWARD</span>
              </div>
              <div className="flex-row-d">
                <div className="rectangle-19" />
                <span className="xaa-d">8xa1055a...163d7844</span>
                <span className="dot-07445">0.07445</span>
              </div>
              <div className="flex-row-a">
                <div className="rectangle-1a" />
                <span className="xaa-d-1b">8xa1055a...163d7844</span>
                <span className="dot-07445-1c">0.07445</span>
              </div>
              <div className="flex-row-dbd">
                <div className="rectangle-1d" />
                <span className="xaa-d-1e">8xa1055a...163d7844</span>
                <span className="dot-07445-1f">0.07445</span>
              </div>
              <div className="flex-row-a-20">
                <div className="rectangle-21" />
                <span className="xaa-d-22">8xa1055a...163d7844</span>
                <span className="dot-07445-23">0.07445</span>
              </div>
              <div className="flex-row-db">
                <div className="rectangle-24" />
                <div className="group-25" />
                <span className="xaa-d-26">8xa1055a...163d7844</span>
                <span className="dot-27">0.07445</span>
              </div>
            </>
          ) : (
            <div className="faq-section">
              <div className="faq-item">
                <div className="faq-item-row">
                  <span className="faq-dot" />
                  <span className="faq-title">WHAT IS THE RETURN OF INVESTMENT(ROI)?</span>
                  <span className="faq-arrow">➜</span>
                </div>
                <div className="faq-desc">After successfully joining, the system will start...</div>
              </div>
              <div className="faq-item">
                <div className="faq-item-row">
                  <span className="faq-dot" />
                  <span className="faq-title">HOW TO EARN REWARD?</span>
                  <span className="faq-arrow">➜</span>
                </div>
                <div className="faq-desc">The cryptocurrency mined every day generates ETH...</div>
              </div>
              <div className="faq-item">
                <div className="faq-item-row">
                  <span className="faq-dot" />
                  <span className="faq-title">IS THERE A REWARD FOR INVITING FRIENDS?</span>
                  <span className="faq-arrow">➜</span>
                </div>
                <div className="faq-desc">Yes, you can invite your friends to join the mining...</div>
              </div>
            </div>
          )}
        </>
      ) : activeBottom === "transaction" ? (
        <Transaction />
      ) : activeBottom === "share" ? (
        <Share />
      ) : (
        <Account />
      )}
      <div className="rectangle-28">
        <div className="flex-row-fc">
          <div className="ellipse-29" />
          <div className="ellipse-2a" />
          <div className="ellipse-2b" />
          <div className="ellipse-2c" />
          <div className="frame-2d" />
          <div className="frame-2e" />
          <div className="frame-2f" />
          <div className="frame-30" />
        </div>
        <div className="flex-row-fb">
          <span
            className={`home-31 ${activeBottom === "home" ? "nav-active" : ""}`}
            onClick={() => setActiveBottom("home")}
          >
            home
          </span>
          <span
            className={`transaction ${
              activeBottom === "transaction" ? "nav-active" : ""
            }`}
            onClick={() => setActiveBottom("transaction")}
          >
            Transaction
          </span>
          <span
            className={`share ${activeBottom === "share" ? "nav-active" : ""}`}
            onClick={() => setActiveBottom("share")}
          >
            Share
          </span>
          <span
            className={`account ${activeBottom === "account" ? "nav-active" : ""}`}
            onClick={() => setActiveBottom("account")}
          >
            Account
          </span>
        </div>
      </div>

      {/* Floating Chat Button */}
      <div className="chat-float-btn" onClick={() => setChatOpen(!chatOpen)}>
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
        </svg>
      </div>

      {/* Chat Window */}
      {chatOpen && (
        <div className="chat-window">
          <div className="chat-header">
            <div className="chat-header-title">Customer Support</div>
            <button className="chat-close-btn" onClick={() => setChatOpen(false)}>×</button>
          </div>
          <div className="chat-messages">
            <div className="chat-message chat-message-bot">
              <div className="message-avatar">🤖</div>
              <div className="message-bubble">
                Hello! How can I help you today?
              </div>
            </div>
          </div>
          <div className="chat-input-wrapper">
            <input 
              type="text" 
              className="chat-input" 
              placeholder="Type your message..."
            />
            <button className="chat-send-btn">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
