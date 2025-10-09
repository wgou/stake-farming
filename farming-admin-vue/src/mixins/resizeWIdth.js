/**
 * 自定义dialog宽度，dialog中form的label宽度
 */
export default{
    data(){
        return{
            layoutType:true,
            labelSize:4
        }
    },
    computed: {
        dialogWidth() {
            if(this.layoutType==true){
                return (this.labelSize*13+10+12)+360+80+'px'
            }else{
                return ((this.labelSize*13+12+10)+360)*2+80+64+'px'
            }
        },
        labelWidth(){
            return this.labelSize*13+30+12+'px'
        }
    },
}