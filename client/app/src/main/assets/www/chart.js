function gen_loc(data) {
    var echart = echarts.init(document.getElementById('chart'))

    option = {
        title : {
            text: '上个月消费场所比例',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series : [
            {
                name: '消费场所',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data: data
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }

    echart.setOption(option)
}

