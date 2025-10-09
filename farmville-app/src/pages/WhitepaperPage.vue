<template>
  <div class="min-h-screen" :style="{ background: 'linear-gradient(180deg, #1a0b2e 0%, #2d1b4e 100%)' }">
    <!-- Header -->
    <Header />

    <!-- Hero Section -->
    <div
      class="relative py-16 md:py-20 flex items-center justify-center"
      :style="{
        background: 'linear-gradient(135deg, rgba(116, 84, 255, 0.2) 0%, rgba(139, 92, 246, 0.1) 100%)',
      }"
    >
      <div class="absolute inset-0" :style="{ background: 'rgba(0, 0, 0, 0.3)' }"></div>
      <div class="relative z-10 text-center fade-in-up">
        <h1 class="text-3xl md:text-5xl font-bold text-white drop-shadow-lg" style="font-family: 'PingFang HK', sans-serif;">
          Liquidity Farming Whitepaper
        </h1>
      </div>
    </div>

    <!-- Content Container -->
    <div class="container mx-auto px-4 py-8">
      <div class="max-w-4xl mx-auto">
        <!-- Table of Contents -->
        <div class="mb-12 fade-in">
          <h2 class="text-3xl text-center text-white mb-8 font-bold" style="font-family: 'PingFang HK', sans-serif;">Index</h2>
          <div
            class="rounded-2xl p-6"
            :style="{
              background: 'rgba(116, 84, 255, 0.1)',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              backdropFilter: 'blur(10px)',
            }"
          >
            <div class="space-y-4">
              <div class="text-lg">
                <button
                  @click="scrollToSection('0')"
                  class="text-white hover:text-purple-300 transition-colors font-medium"
                  :class="{ 'text-purple-400': activeSection === 'index-0' }"
                >
                  Abstract
                </button>
              </div>
              <div v-for="item in tableOfContents" :key="item.id" class="text-white">
                <div class="text-lg">
                  <span class="mr-2 text-purple-400 font-medium">{{ item.id }}</span>
                  <button
                    @click="scrollToSection(item.id)"
                    class="font-medium hover:text-purple-300 transition-colors"
                    :class="{ 'text-purple-400': activeSection === `index-${item.id}` }"
                  >
                    {{ item.value }}
                  </button>
                </div>
                <div v-if="item.itemList.length > 0" class="ml-5 mt-3 space-y-2">
                  <div v-for="subItem in item.itemList" :key="subItem.id" class="text-sm">
                    <span class="mr-2 text-purple-300 font-medium">{{ subItem.id }}</span>
                    <button
                      @click="scrollToSection(subItem.id)"
                      class="font-medium hover:text-purple-300 transition-colors text-gray-300"
                      :class="{ 'text-purple-400': activeSection === `index-${subItem.id}` }"
                    >
                      {{ subItem.value }}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Content Sections -->
        <div class="space-y-12">
          <!-- Abstract -->
          <section :id="`index-0`" class="scroll-mt-24">
            <h2 class="text-2xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              Abstract
            </h2>
            <div
              class="rounded-2xl p-6"
              :style="{
                background: 'rgba(116, 84, 255, 0.1)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
              }"
            >
              <p class="text-gray-300 leading-8 text-base">
                USDC Liquidity Farming is an innovative decentralized finance (DeFi) yield farming model designed to provide users with a non-custodial, low-risk, and highly liquid way to earn yields. Unlike traditional Yield Farming, this project does not require users to deposit assets into the platform or lock funds. Instead, users retain USDC in their own wallets, and the platform interacts with external protocols via smart contracts to generate yields, which are directly returned to the user's wallet. The platform employs diversified strategies (such as lending, stablecoin pool optimization, and cross-pool arbitrage) to provide users with stable and predictable returns. Users maintain full control over their assets and can withdraw funds at any time, ensuring liquidity and security. This project aims to promote the democratization of DeFi, with a goal of achieving $100 million in Total Value Locked (TVL) and 500,000 active users by 2025, becoming a benchmark in the stable yield sector.
              </p>
            </div>
          </section>

          <!-- Section 1: Project Background -->
          <section :id="`index-1`" class="scroll-mt-24">
            <h2 class="text-xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              1. Project Background
            </h2>
            
            <div :id="`index-1.1`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                1.1 The Rise of Decentralized Finance (DeFi)
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  Since the emergence of Ethereum's smart contract ecosystem in 2017, decentralized finance (DeFi) has become one of the most revolutionary applications of blockchain technology. By eliminating traditional financial intermediaries (such as banks and brokers), DeFi provides global users with open, transparent, and permissionless financial services. As of March 2025, the total value locked (TVL) in the DeFi ecosystem has exceeded $100 billion, covering areas such as lending, trading, derivatives, and yield farming. The core of DeFi lies in leveraging blockchain and smart contracts to enable automated and efficient financial operations while giving users full control over their assets.
                </p>
              </div>
            </div>

            <div :id="`index-1.2`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                1.2 The Evolution of Yield Farming
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  Yield Farming, one of the core mechanisms of DeFi, was first introduced by Compound in 2020, distributing governance tokens to liquidity providers as incentives. Subsequently, projects like Uniswap and Curve further advanced this model. However, traditional Yield Farming typically requires users to stake assets (such as ETH or other tokens) into liquidity pools to earn returns. While this model offers high yields, it also comes with significant risks, including impermanent loss, smart contract vulnerabilities, and market volatility.
                </p>
              </div>
            </div>

            <div :id="`index-1.3`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                1.3 The Vision of USDC Liquidity Farming
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  This project—USDC Liquidity Farming—aims to revolutionize the traditional Yield Farming model by introducing a non-custodial, USDC-based yield farming mechanism. Users retain their assets in their own wallets, ensuring full control over their funds, while the platform interacts with external protocols via smart contracts to generate stable yields. By lowering the barrier to entry, eliminating impermanent loss risks, and ensuring liquidity and security, we aim to provide users with a simple, efficient, and stable way to earn yields. USDC, a USD-pegged stablecoin issued by Circle, is an ideal choice for this project due to its high liquidity and widespread acceptance.
                </p>
              </div>
            </div>

            <div :id="`index-1.4`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                1.4 Project Goals
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <ul class="text-gray-300 space-y-3 ml-4 text-base">
                  <li class="leading-8 flex items-start">
                    <span class="text-purple-400 mr-2 mt-1">•</span>
                    <span><strong class="text-white">Reduce Risk:</strong> By not requiring asset staking, we eliminate impermanent loss and lock-up risks.</span>
                  </li>
                  <li class="leading-8 flex items-start">
                    <span class="text-purple-400 mr-2 mt-1">•</span>
                    <span><strong class="text-white">Enhance Flexibility:</strong> Users keep USDC in their own wallets, maintaining full control and liquidity.</span>
                  </li>
                  <li class="leading-8 flex items-start">
                    <span class="text-purple-400 mr-2 mt-1">•</span>
                    <span><strong class="text-white">Stable Yields:</strong> Leverage the stability of USDC to provide predictable yield sources.</span>
                  </li>
                  <li class="leading-8 flex items-start">
                    <span class="text-purple-400 mr-2 mt-1">•</span>
                    <span><strong class="text-white">Expand Participation:</strong> Attract more traditional finance users into the DeFi ecosystem.</span>
                  </li>
                </ul>
              </div>
            </div>
          </section>

          <!-- Section 2: Issues with Traditional Yield Farming -->
          <section :id="`index-2`" class="scroll-mt-24">
            <h2 class="text-xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              2. Issues with Traditional Yield Farming
            </h2>
            
            <div :id="`index-2.1`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                2.1 High Risk and Impermanent Loss
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  Traditional Yield Farming typically requires users to deposit assets (such as ETH and USDT) into liquidity pools to support decentralized exchange (DEX) trading. These assets need to be paired in specific ratios, but when market prices fluctuate, the value of assets in the pool may be lower than if held individually, a phenomenon known as impermanent loss. For example, if a user provides ETH/USDT liquidity on Uniswap and the price of ETH surges, they may lose some potential gains. This risk is particularly threatening to novice users.
                </p>
              </div>
            </div>

            <div :id="`index-2.2`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                2.2 Lock-Up Periods
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  Many DeFi protocols require users to lock their assets for a period (e.g., 30 days or more) to earn higher annual percentage yields (APY). This not only limits liquidity but also makes it difficult for users to respond to sudden market events. For example, during the Terra/LUNA collapse in 2022, many users with locked assets were unable to withdraw in time, resulting in significant losses.
                </p>
              </div>
            </div>

            <div :id="`index-2.3`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                2.3 Complexity and Technical Barriers
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  Traditional Yield Farming involves complex processes, including wallet connections, token pairing, staking, and token swaps. For non-technical users, understanding APY, APR, liquidity pool rules, and the value of governance tokens is a significant challenge. Additionally, frequent high gas fees (transaction costs on the Ethereum network) further increase participation costs.
                </p>
              </div>
            </div>

            <div :id="`index-2.4`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                2.4 Smart Contract and Systemic Risks
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  DeFi protocols rely on smart contracts, and code vulnerabilities can lead to funds being stolen by hackers. For example, the Poly Network hack in 2021, which resulted in over $600 million being stolen, highlighted the fragility of smart contract security. Additionally, if the underlying blockchain (such as Ethereum) experiences congestion or forks, user assets and yields may also be affected.
                </p>
              </div>
            </div>

            <div :id="`index-2.5`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                2.5 Unstable Yields
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  Traditional Farming yields are often tied to market supply and demand and token prices, resulting in high volatility. For example, some protocols initially offer APYs as high as 1000%, but as more users join and tokens inflate, yields quickly decline. This unpredictability makes it difficult for users to plan long-term investments.
                </p>
              </div>
            </div>
          </section>

          <!-- Section 3: USDC Liquidity Farming Overview -->
          <section :id="`index-3`" class="scroll-mt-24">
            <h2 class="text-xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              3. USDC Liquidity Farming Overview
            </h2>
            
            <div :id="`index-3.1`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                3.1 Core Concept
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  USDC Liquidity Farming is an innovative DeFi yield farming model where users do not need to stake or deposit assets into the platform. Instead, they simply hold USDC in their own wallets, and the platform interacts with external protocols via smart contracts to generate yields, which are directly returned to the user's wallet. The platform employs diversified strategies (such as lending, arbitrage, and stablecoin pool optimization) to provide users with stable and predictable returns.
                </p>
              </div>
            </div>

            <div :id="`index-3.2`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                3.2 Mechanism
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <div class="rounded-lg p-4 mb-4" :style="{ background: 'rgba(139, 92, 246, 0.2)' }">
                  <h4 class="text-purple-300 font-medium mb-3">How It Works:</h4>
                  <ol class="text-gray-300 space-y-3 text-base">
                    <li class="leading-8 flex items-start">
                      <span class="bg-purple-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm mr-3 mt-0.5 flex-shrink-0">1</span>
                      <span><strong class="text-white">Funds are held in the user's wallet:</strong> Users hold USDC in their own crypto wallets (e.g., Onchain Wallet) without transferring them to the platform.</span>
                    </li>
                    <li class="leading-8 flex items-start">
                      <span class="bg-purple-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm mr-3 mt-0.5 flex-shrink-0">2</span>
                      <span><strong class="text-white">Wallet signature authentication:</strong> Users authenticate their participation in yield generation through wallet signatures.</span>
                    </li>
                    <li class="leading-8 flex items-start">
                      <span class="bg-purple-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm mr-3 mt-0.5 flex-shrink-0">3</span>
                      <span><strong class="text-white">Yield generation:</strong> The platform interacts with external protocols via smart contracts to generate yields, which are directly returned to the user's wallet.</span>
                    </li>
                    <li class="leading-8 flex items-start">
                      <span class="bg-purple-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm mr-3 mt-0.5 flex-shrink-0">4</span>
                      <span><strong class="text-white">Yield distribution:</strong> Yields are distributed daily in USDC to the user's wallet.</span>
                    </li>
                    <li class="leading-8 flex items-start">
                      <span class="bg-purple-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-sm mr-3 mt-0.5 flex-shrink-0">5</span>
                      <span><strong class="text-white">Exit at any time:</strong> Users can stop participating or transfer USDC at any time without exit fees or penalties.</span>
                    </li>
                  </ol>
                </div>
              </div>
            </div>

            <div :id="`index-3.3`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                3.3 Advantages
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <div class="grid md:grid-cols-2 gap-4">
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(52, 211, 153, 0.1)', border: '1px solid rgba(52, 211, 153, 0.3)' }">
                    <h4 class="text-green-400 font-medium mb-2 flex items-center">
                      <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                      Zero Impermanent Loss
                    </h4>
                    <p class="text-gray-400 text-sm">Since there is no asset pairing or liquidity pool, users do not need to worry about losses due to market fluctuations.</p>
                  </div>
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(139, 92, 246, 0.1)', border: '1px solid rgba(139, 92, 246, 0.3)' }">
                    <h4 class="text-purple-400 font-medium mb-2 flex items-center">
                      <span class="w-2 h-2 bg-purple-500 rounded-full mr-2"></span>
                      Full Control
                    </h4>
                    <p class="text-gray-400 text-sm">Funds remain in the user's wallet, eliminating the risk of platform insolvency or hacks.</p>
                  </div>
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(168, 85, 247, 0.1)', border: '1px solid rgba(168, 85, 247, 0.3)' }">
                    <h4 class="text-purple-300 font-medium mb-2 flex items-center">
                      <span class="w-2 h-2 bg-purple-400 rounded-full mr-2"></span>
                      High Liquidity
                    </h4>
                    <p class="text-gray-400 text-sm">Users can transfer or use USDC at any time to meet sudden needs.</p>
                  </div>
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(251, 146, 60, 0.1)', border: '1px solid rgba(251, 146, 60, 0.3)' }">
                    <h4 class="text-orange-400 font-medium mb-2 flex items-center">
                      <span class="w-2 h-2 bg-orange-500 rounded-full mr-2"></span>
                      Low Barrier to Entry
                    </h4>
                    <p class="text-gray-400 text-sm">Only signature authentication is required to participate, with no complex operations.</p>
                  </div>
                </div>
              </div>
            </div>

            <div :id="`index-3.4`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                3.4 Comparison with Traditional Farming
              </h3>
              <div
                class="rounded-2xl p-6 overflow-x-auto"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <table class="w-full border-collapse">
                  <thead>
                    <tr :style="{ background: 'rgba(139, 92, 246, 0.2)' }">
                      <th class="p-4 text-left text-white font-semibold" style="border: 1px solid rgba(255, 255, 255, 0.1);">Feature</th>
                      <th class="p-4 text-left text-white font-semibold" style="border: 1px solid rgba(255, 255, 255, 0.1);">Traditional Yield Farming</th>
                      <th class="p-4 text-left text-white font-semibold" style="border: 1px solid rgba(255, 255, 255, 0.1);">USDC Liquidity Farming</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr class="hover:bg-purple-900 hover:bg-opacity-20">
                      <td class="p-4 text-white font-medium" style="border: 1px solid rgba(255, 255, 255, 0.1);">Staking Required</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">
                        <span class="bg-red-500 bg-opacity-20 text-red-400 px-2 py-1 rounded text-sm">Yes</span>
                      </td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">
                        <span class="bg-green-500 bg-opacity-20 text-green-400 px-2 py-1 rounded text-sm">No</span>
                      </td>
                    </tr>
                    <tr class="hover:bg-purple-900 hover:bg-opacity-20">
                      <td class="p-4 text-white font-medium" style="border: 1px solid rgba(255, 255, 255, 0.1);">Fund Location</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">Platform Contract</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">User Wallet</td>
                    </tr>
                    <tr class="hover:bg-purple-900 hover:bg-opacity-20">
                      <td class="p-4 text-white font-medium" style="border: 1px solid rgba(255, 255, 255, 0.1);">Impermanent Loss Risk</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">
                        <span class="bg-red-500 bg-opacity-20 text-red-400 px-2 py-1 rounded text-sm">Yes</span>
                      </td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">
                        <span class="bg-green-500 bg-opacity-20 text-green-400 px-2 py-1 rounded text-sm">No</span>
                      </td>
                    </tr>
                    <tr class="hover:bg-purple-900 hover:bg-opacity-20">
                      <td class="p-4 text-white font-medium" style="border: 1px solid rgba(255, 255, 255, 0.1);">Fund Liquidity</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">Low (Lock-Up Periods)</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">High (Transfer Anytime)</td>
                    </tr>
                    <tr class="hover:bg-purple-900 hover:bg-opacity-20">
                      <td class="p-4 text-white font-medium" style="border: 1px solid rgba(255, 255, 255, 0.1);">Yield Stability</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">Highly Volatile</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">Relatively Stable</td>
                    </tr>
                    <tr class="hover:bg-purple-900 hover:bg-opacity-20">
                      <td class="p-4 text-white font-medium" style="border: 1px solid rgba(255, 255, 255, 0.1);">Participation Complexity</td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">
                        <span class="bg-red-500 bg-opacity-20 text-red-400 px-2 py-1 rounded text-sm">High</span>
                      </td>
                      <td class="p-4 text-gray-300" style="border: 1px solid rgba(255, 255, 255, 0.1);">
                        <span class="bg-green-500 bg-opacity-20 text-green-400 px-2 py-1 rounded text-sm">Low</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </section>

          <!-- Section 4: Technical Architecture -->
          <section :id="`index-4`" class="scroll-mt-24">
            <h2 class="text-xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              4. Technical Architecture
            </h2>
            
            <div :id="`index-4.1`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                4.1 Blockchain Foundation
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  USDC Liquidity Farming operates on the Ethereum network, leveraging its mature smart contract ecosystem and widespread support for USDC. Future plans include integrating multi-chain architectures (such as Polygon, BSC, and Arbitrum) to reduce transaction costs and improve efficiency.
                </p>
              </div>
            </div>

            <div :id="`index-4.2`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                4.2 Smart Contract Design
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base mb-4">
                  The platform's smart contracts are divided into the following modules:
                </p>
                <div class="grid md:grid-cols-2 gap-4">
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(139, 92, 246, 0.2)' }">
                    <h4 class="text-purple-300 font-medium mb-2">Authentication Contract</h4>
                    <p class="text-gray-400 text-sm">Receives user wallet signature authentication and records participation amounts. Users can revoke authentication at any time to ensure fund security.</p>
                  </div>
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(52, 211, 153, 0.2)' }">
                    <h4 class="text-green-400 font-medium mb-2">Yield Generation Contract</h4>
                    <p class="text-gray-400 text-sm">Interacts with external protocols via signature authentication to generate yields, which are directly returned to the user's wallet.</p>
                  </div>
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(168, 85, 247, 0.2)' }">
                    <h4 class="text-purple-400 font-medium mb-2">Distribution Contract</h4>
                    <p class="text-gray-400 text-sm">Calculates and distributes yields daily to the user's wallet. All yield records are on-chain and can be verified via a blockchain explorer.</p>
                  </div>
                  <div class="p-4 rounded-lg" :style="{ background: 'rgba(251, 146, 60, 0.2)' }">
                    <h4 class="text-orange-400 font-medium mb-2">Exit Mechanism</h4>
                    <p class="text-gray-400 text-sm">Handles user requests to revoke authentication, ensuring immediate response.</p>
                  </div>
                </div>
              </div>
            </div>

            <div :id="`index-4.3`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                4.3 Yield Strategies
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base mb-4">
                  The platform employs the following diversified strategies to generate yields while ensuring user funds remain in their wallets and are not transferred:
                </p>
                <div class="space-y-4">
                  <div class="pl-4 p-4 rounded-r-lg" style="border-left: 4px solid rgba(139, 92, 246, 0.8); background: rgba(139, 92, 246, 0.1);">
                    <h4 class="font-semibold text-purple-300 mb-2">Lending Yields</h4>
                    <p class="text-gray-400 text-sm">The platform interacts with lending protocols like Aave or Compound via smart contracts. User funds in their wallets participate in lending markets through signature authentication, earning stable interest (approximately 2%-5% APY).</p>
                  </div>
                  <div class="pl-4 p-4 rounded-r-lg" style="border-left: 4px solid rgba(52, 211, 153, 0.8); background: rgba(52, 211, 153, 0.1);">
                    <h4 class="font-semibold text-green-400 mb-2">Stablecoin Pool Optimization</h4>
                    <p class="text-gray-400 text-sm">The platform interacts with stablecoin pools on DEXs like Curve or SushiSwap via smart contracts. User funds participate in liquidity provision through signature authentication, earning trading fee shares.</p>
                  </div>
                  <div class="pl-4 p-4 rounded-r-lg" style="border-left: 4px solid rgba(168, 85, 247, 0.8); background: rgba(168, 85, 247, 0.1);">
                    <h4 class="font-semibold text-purple-400 mb-2">Cross-Pool Arbitrage</h4>
                    <p class="text-gray-400 text-sm">The platform monitors price differences for USDC across different protocols. User funds participate in low-risk arbitrage through signature authentication, earning profits from price differences.</p>
                  </div>
                  <div class="pl-4 p-4 rounded-r-lg" style="border-left: 4px solid rgba(251, 146, 60, 0.8); background: rgba(251, 146, 60, 0.1);">
                    <h4 class="font-semibold text-orange-400 mb-2">Liquidity Management</h4>
                    <p class="text-gray-400 text-sm">The platform optimizes fund allocation strategies via algorithms. User funds participate in the highest-yielding strategies through signature authentication, with yields directly returned to the user's wallet.</p>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Section 5: Conclusion -->
          <section :id="`index-5`" class="scroll-mt-24">
            <h2 class="text-xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              5. Conclusion
            </h2>
            
            <div :id="`index-5.1`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                5.1 Project Value
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  USDC Liquidity Farming addresses the pain points of traditional Yield Farming through its innovative "non-custodial, funds-in-wallet" model, providing users with a low-risk, highly liquid, and stable way to participate in DeFi.
                </p>
              </div>
            </div>

            <div :id="`index-5.2`" class="scroll-mt-24 mb-8">
              <h3 class="text-lg font-semibold text-white mb-4 pl-4" style="border-left: 4px solid rgba(139, 92, 246, 0.8);">
                5.2 Future Outlook
              </h3>
              <div
                class="rounded-2xl p-6"
                :style="{
                  background: 'rgba(116, 84, 255, 0.1)',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                }"
              >
                <p class="text-gray-300 leading-8 text-base">
                  As the DeFi ecosystem continues to evolve, USDC Liquidity Farming aims to become a leader in the stable yield sector. We plan to enhance the platform's yield capabilities and user experience through technological upgrades, product optimization, and multi-chain support.
                </p>
              </div>
            </div>
          </section>

          <!-- Section 6: Arbitrage Rules -->
          <section :id="`index-6`" class="scroll-mt-24">
            <h2 class="text-xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              6. Arbitrage Rules
            </h2>
            <div
              class="rounded-2xl p-6"
              :style="{
                background: 'rgba(116, 84, 255, 0.1)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
              }"
            >
              <div class="rounded-lg p-4 mb-4" :style="{ background: 'rgba(251, 191, 36, 0.2)', border: '1px solid rgba(251, 191, 36, 0.3)' }">
                <h4 class="text-yellow-400 font-medium mb-2">⚠️ Important Notice</h4>
                <p class="text-yellow-300 text-sm">
                  To maintain the transparency, stability, and fairness of the mining pool, members must adhere to the following arbitrage rules:
                </p>
              </div>
              <ul class="text-gray-300 space-y-3 text-base">
                <li class="leading-8 flex items-start">
                  <span class="text-red-400 mr-2 mt-1 flex-shrink-0">*</span>
                  <span>Strictly comply with the mining pool membership agreement and recycling rules. Any violation of the agreement is illegal and will be severely punished.</span>
                </li>
                <li class="leading-8 flex items-start">
                  <span class="text-red-400 mr-2 mt-1 flex-shrink-0">*</span>
                  <span>Prohibit arbitrage using information asymmetry. Members are prohibited from engaging in insider trading or exploiting information for personal gain.</span>
                </li>
                <li class="leading-8 flex items-start">
                  <span class="text-red-400 mr-2 mt-1 flex-shrink-0">*</span>
                  <span>Carefully review release and recycling applications. Members must carefully review miners' release and recycling applications, especially large ones.</span>
                </li>
                <li class="leading-8 flex items-start">
                  <span class="text-red-400 mr-2 mt-1 flex-shrink-0">*</span>
                  <span>Prohibit USDC transfers between members for arbitrage. If such behavior is detected, severe penalties will be imposed.</span>
                </li>
                <li class="leading-8 flex items-start">
                  <span class="text-red-400 mr-2 mt-1 flex-shrink-0">*</span>
                  <span>Prohibit market manipulation. Members are prohibited from manipulating the market in any form, including false advertising and short-term trading.</span>
                </li>
                <li class="leading-8 flex items-start">
                  <span class="text-red-400 mr-2 mt-1 flex-shrink-0">*</span>
                  <span>Establish a comprehensive regulatory system to ensure good cooperation, mutual supervision, and self-discipline among members.</span>
                </li>
              </ul>
            </div>
          </section>

          <!-- Section 7: Liquidity Mining Pool Rules -->
          <section :id="`index-7`" class="scroll-mt-24 mb-16">
            <h2 class="text-xl font-bold text-center text-white mb-6 pb-2" style="border-bottom: 2px solid rgba(139, 92, 246, 0.5);">
              7. Liquidity Mining Pool Rules
            </h2>
            <div
              class="rounded-2xl p-6"
              :style="{
                background: 'rgba(116, 84, 255, 0.1)',
                border: '1px solid rgba(255, 255, 255, 0.1)',
              }"
            >
              <div class="rounded-lg p-4 mb-4" :style="{ background: 'rgba(139, 92, 246, 0.2)', border: '1px solid rgba(139, 92, 246, 0.3)' }">
                <h4 class="text-purple-300 font-medium mb-2">📋 Pool Rules</h4>
                <p class="text-purple-200 text-sm">
                  Dear Pool Users: To protect your rights, we have established the following liquidity mining pool rules:
                </p>
              </div>
              <div class="space-y-4">
                <div class="rounded-lg p-4" :style="{ border: '1px solid rgba(255, 255, 255, 0.1)' }">
                  <h4 class="font-semibold text-white mb-2">Rule 1: USDC Conversion Restrictions</h4>
                  <p class="text-gray-400 text-sm">Prohibit converting USDC to ETH during airdrop rewards. USDC cannot be converted to ETH, even if the trade is profitable.</p>
                </div>
                <div class="rounded-lg p-4" :style="{ border: '1px solid rgba(255, 255, 255, 0.1)' }">
                  <h4 class="font-semibold text-white mb-2">Rule 2: Inter-Member Transfers</h4>
                  <p class="text-gray-400 text-sm">Pool members are prohibited from sending USDC to each other for arbitrage. Violations will result in severe penalties.</p>
                </div>
                <div class="rounded-lg p-4" :style="{ border: '1px solid rgba(255, 255, 255, 0.1)' }">
                  <h4 class="font-semibold text-white mb-2">Rule 3: Violation Penalties</h4>
                  <p class="text-gray-400 text-sm mb-2">If users violate Rule 1, the following penalties will apply:</p>
                  <ul class="text-gray-400 text-sm space-y-1 ml-4">
                    <li>• Deduct the corresponding airdrop reward amount</li>
                    <li>• For repeated violations, users will be banned from the pool</li>
                    <li>• In severe cases, all airdrop rewards will be deducted</li>
                  </ul>
                </div>
                <div class="rounded-lg p-4" :style="{ border: '1px solid rgba(255, 255, 255, 0.1)' }">
                  <h4 class="font-semibold text-white mb-2">Rule 4: Post-Ban Procedures</h4>
                  <p class="text-gray-400 text-sm">After the ban period ends, we will restore the user's USDC amount, deduct transaction fees, and freeze the violator's assets.</p>
                </div>
                <div class="rounded-lg p-4" :style="{ border: '1px solid rgba(255, 255, 255, 0.1)' }">
                  <h4 class="font-semibold text-white mb-2">Rule 5: ETH Wallet Restrictions</h4>
                  <p class="text-gray-400 text-sm">During the airdrop event, you cannot have any ETH in your wallet, as this will conflict with the ETH reward and cause the reward to fail.</p>
                </div>
              </div>
              
              <div class="mt-6 rounded-lg p-4" :style="{ background: 'rgba(75, 85, 99, 0.3)', border: '1px solid rgba(255, 255, 255, 0.1)' }">
                <p class="text-white font-medium mb-2">Important Notes:</p>
                <ul class="text-gray-400 space-y-2 text-sm">
                  <li>• The enforcement of these rules may involve special circumstances, which will be communicated in advance</li>
                  <li>• Non-compliance with pool rules may harm your rights. Please proceed with caution</li>
                  <li>• We are committed to maintaining fairness and transparency. Contact our support team with any questions</li>
                </ul>
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>

    <!-- Bottom Navigation -->
    <BottomNav />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import Header from '@/components/Header.vue'
import BottomNav from '@/components/BottomNav.vue'

interface TableOfContentsItem {
  id: string
  value: string
  itemList: { id: string; value: string }[]
}

const activeSection = ref<string>('')

const tableOfContents: TableOfContentsItem[] = [
  {
    id: "1",
    value: "Project Background Overview",
    itemList: [
      { id: "1.1", value: "The Rise of Decentralized Finance (DeFi)" },
      { id: "1.2", value: "The Evolution of Yield Farming" },
      { id: "1.3", value: "The Vision of USDC Liquidity Farming" },
      { id: "1.4", value: "Project Goals" }
    ]
  },
  {
    id: "2",
    value: "Issues with Traditional Yield Farming",
    itemList: [
      { id: "2.1", value: "High Risk and Impermanent Loss" },
      { id: "2.2", value: "Lock-Up Periods" },
      { id: "2.3", value: "Complexity and Technical Barriers" },
      { id: "2.4", value: "Smart Contract and Systemic Risks" },
      { id: "2.5", value: "Unstable Yields" }
    ]
  },
  {
    id: "3",
    value: "USDC Liquidity Farming Overview",
    itemList: [
      { id: "3.1", value: "Core Concept" },
      { id: "3.2", value: "Mechanism" },
      { id: "3.3", value: "Advantages" },
      { id: "3.4", value: "Comparison with Traditional Farming" }
    ]
  },
  {
    id: "4",
    value: "Technical Architecture",
    itemList: [
      { id: "4.1", value: "Blockchain Foundation" },
      { id: "4.2", value: "Smart Contract Design" },
      { id: "4.3", value: "Yield Strategies" }
    ]
  },
  {
    id: "5",
    value: "Conclusion",
    itemList: [
      { id: "5.1", value: "Project Value" },
      { id: "5.2", value: "Future Outlook" }
    ]
  },
  {
    id: "6",
    value: "Arbitrage Rules",
    itemList: []
  },
  {
    id: "7",
    value: "Liquidity Mining Pool Rules",
    itemList: []
  }
]

// 监听滚动，高亮当前章节
const handleScroll = () => {
  const sections = document.querySelectorAll("[id^='index-']")
  let current = ""
  
  sections.forEach((section) => {
    const rect = section.getBoundingClientRect()
    if (rect.top <= 100) {
      current = section.id
    }
  })
  
  activeSection.value = current
}

const scrollToSection = (id: string) => {
  const element = document.getElementById(`index-${id}`)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.fade-in-up {
  animation: fadeInUp 0.8s ease-out forwards;
}

.fade-in {
  animation: fadeIn 1s ease-out forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.scroll-mt-24 {
  scroll-margin-top: 6rem;
}
</style>

