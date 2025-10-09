const getters = {
    collect: state => state.select.collect,
    Freeze: state => state.select.Freeze,//资金是否冻结
    approves: state => state.select.approves,//是否授权
    invite: state => state.select.invite,//是否推荐奖励
    invitesList: state => state.select.invitesList,//招聘员下拉列表
    kills: state => state.select.kills,//杀掉
    pools: state => state.select.pools,//奖金池
    reals: state => state.select.reals,//真实/虚拟
    withdrawList: state => state.select.withdrawList,//提现状态
    userId: state => state.user.id,//当前用户id
}
export default getters