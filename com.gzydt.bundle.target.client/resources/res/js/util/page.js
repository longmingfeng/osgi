/**
 * 生成底部页码方法
 * @param currentpage 当前页码
 * @param totalCount  数据总数
 * @param pageSize	每页大小
 * @param refreshFun 点击页码刷新页码的方法名称
 * @param parameter 可选参数
 */
function createNav(currentpage,totalCount,pageSize,refreshFun,parameter){
	$("#pageUL").empty();
	var pagecount = totalCount;
	var _pagesize = pageSize;
	var _currentpage = currentpage;
	var counts,pagehtml="";
	if(pagecount%_pagesize==0){
		counts = parseInt(pagecount/_pagesize);
	}else{
		counts = parseInt(pagecount/_pagesize)+1;
	}
	var totalPage = pagecount % _pagesize == 0 ? pagecount / _pagesize : Math.ceil(pagecount / _pagesize);
	pagehtml+= '<li style="float:left;font-size: 12px;height:35px; line-height:35px;margin-right:20px">'+
			   '<div>&nbsp;&nbsp;共：'+totalPage+'&nbsp;页</div>'+
			   '</li>';
	if(_currentpage>1){
		pagehtml+= '<li><a href="#" onclick="'+refreshFun+'('+(_currentpage-1)+','+parameter+')">上一页</a></li>';
	}
	for(var i=0;i<counts;i++){
		if(i>=(_currentpage-3) && i<(_currentpage+3)){
			if(i==_currentpage-1){
				pagehtml+= '<li class="active"><a href="#" onclick="'+refreshFun+'('+(i+1)+','+parameter+')">'+(i+1)+'</a></li>';
			}else{
				pagehtml+= '<li><a href="#" onclick="'+refreshFun+'('+(i+1)+','+parameter+')">'+(i+1)+'</a></li>';
			}
		}
	}
	if(_currentpage<counts){
		pagehtml+= '<li><a href="#" onclick="'+refreshFun+'('+(_currentpage+1)+','+parameter+')">下一页</a></li>';
	}
	pagehtml+='<div style="float:left;height:35px; line-height:35px;margin-left:10px">'+
	'<span class="pgo">跳转<input type="text" id="goValu">页</span>'+
	'<a href="#" class="pbtn" onclick="'+refreshFun+'()">GO</a></div>';
	$("#pageUL").append(pagehtml);
}