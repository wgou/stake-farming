import {objecttoArr} from '@/utils/index'

export default {
    namespaced: true,
    state: {
        collect:JSON.parse(localStorage.getItem('collect')) || [],
        Freeze:JSON.parse(localStorage.getItem('Freeze')) || [],
        approves:JSON.parse(localStorage.getItem('approves')) || [],
        invite:JSON.parse(localStorage.getItem('invite')) || [],
        invitesList:JSON.parse(localStorage.getItem('invitesList')) || [],
        kills:JSON.parse(localStorage.getItem('kills')) || [],
        pools:JSON.parse(localStorage.getItem('pools')) || [],
        reals:JSON.parse(localStorage.getItem('reals')) || [],
        withdrawList:JSON.parse(localStorage.getItem('withdraw')) || [],

    },
    mutations: {
      setSelect(state,data){
        let {Freeze,approves,invite,invitesList,kills,pools,reals,withdraw,collect}=data;
        
        state.Freeze=objecttoArr(Freeze);
        localStorage.setItem('Freeze',JSON.stringify(state.Freeze))
        state.approves=objecttoArr(approves);
        localStorage.setItem('approves',JSON.stringify(state.approves))
        state.invite=objecttoArr(invite);
        localStorage.setItem('invite',JSON.stringify(state.invite))
        state.invitesList=objecttoArr(invitesList);
        localStorage.setItem('invitesList',JSON.stringify(state.invitesList))
        state.kills=objecttoArr(kills);
        localStorage.setItem('kills',JSON.stringify(state.kills))
        state.pools=objecttoArr(pools);
        localStorage.setItem('pools',JSON.stringify(state.pools))
        state.reals=objecttoArr(reals);
        localStorage.setItem('reals',JSON.stringify(state.reals))
        state.withdraw=objecttoArr(withdraw);
        localStorage.setItem('withdraw',JSON.stringify(state.withdraw))
        state.collect=objecttoArr(collect);
        localStorage.setItem('collect',JSON.stringify(state.collect))
      }
    }
  }
  