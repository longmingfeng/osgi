//关键字
var keyword="";
//是否停用
var isStop=-1;
//所属类型
var belongType=0;
//初始化加载
window.onload=function(){
	load(1,10);
	$("#go").click(function(){
		var goValue = $('#goValu').val();
		var reNum = /^\d*$/;
		//输入的不是数字或者为空
		if(!reNum.test(goValue) || goValue==""){
			goValue=1;
		}
		
		load(goValue,10)
	});
}
//展示或隐藏搜索
function showSearch(){
	if($("#searchList_btn").html()=="展开搜索列表"){
		$("#searchInfo").show();
		$("#searchList_btn").html("隐藏搜索列表");
	}else{
		$("#searchInfo").hide();
		$("#searchList_btn").html("展开搜索列表");
	}
}
//搜索元数据
function searchMetaData(keywords){
	if(keywords=="基础元数据"){
		belongType=1;
		$("#belongType").html("基础元数据<span onclick='removeSearch(\"0\")' class='glyphicon glyphicon-remove'></span>");
		$("#belongType").show();
	}else if(keywords=="类型元数据"){
		belongType=2;
		$("#belongType").html("类型元数据<span onclick='removeSearch(\"0\")' class='glyphicon glyphicon-remove'></span>");
		$("#belongType").show();
	}else if(keywords=="资源元数据"){
		belongType=3;
		$("#belongType").html("资源元数据<span onclick='removeSearch(\"0\")' class='glyphicon glyphicon-remove'></span>");
		$("#belongType").show();
	}else if(keywords=="启用"){
		isStop=1;
		$("#isStop").html("启用<span onclick='removeSearch(\"1\")' class='glyphicon glyphicon-remove'></span>");
		$("#isStop").show();
	}else if(keywords=="停用"){
		isStop=0;
		$("#isStop").html("停用<span onclick='removeSearch(\"1\")' class='glyphicon glyphicon-remove'></span>");
		$("#isStop").show();
	}else{
		keyword=$("#keyword").val();
		if(keyword!=""&&keyword!=null){
			$("#keyWord_btn").html(keyword+"<span onclick='removeSearch(\"2\")' class='glyphicon glyphicon-remove'></span>");
			$("#keyWord_btn").show();
		}
	}
	load(0,10);
}
//移除搜索
function removeSearch(id){
	if(id==0){
		belongType=0;
		$("#belongType").html("");
		$("#belongType").hide();
	}
	if(id==1){
		isStop=-1;
		$("#isStop").html("");
		$("#isStop").hide();
	}
	if(id==2){
		keyword="";
		$("#keyWord_btn").html("");
		$("#keyWord_btn").hide();
	}
	load(0,10);
}
//加载元数据
function load(pageNum,pageSize){
	$("#list").empty();
	var total = 0;
	if(belongType==1||belongType==0){
		$.ajax({
			type : "GET",
			global : false,
			async : false,
			url : "http://" + window.location.host
					+ "/mateDataManager/getAll?pageNum=" + pageNum + "&pageSize="
					+ pageSize+"&isStop="+isStop+"&keyword="+keyword,
			success : function(data) {
				var result = eval("(" + data + ")");
				metadatas = result["data"];
				for (var i = 0; i < metadatas.length; i++) {
					var state = metadatas[i]["state"] == "1" ? "启用" : "停用";
					var opState = metadatas[i]["state"] == "1" ? "停用" : "启用";
					var opStateVal = metadatas[i]["state"] == "1" ? "0" : "1";
					$("#list").append(
							"<tr>" + "<td class='active'>" + metadatas[i]["id"]
									+ "</td>" + "<td class='success'>"
									+ metadatas[i]["name"] + "</td>"
									+ "<td class='warning'>" + metadatas[i]["type"]
									+ "</td>" + "<td class='danger'>"
									+ metadatas[i]["author"] + "</td>"
									+ "<td class='info'>" + state + "</td>"
									+ "<td class='active'>"
									+ metadatas[i]["content"] + "</td>"
									+ "<td class='success'>基础元数据</td>" + "</tr>");
				}
				total += parseInt(result["total"]);
	
			}
		});
	}
	if(belongType==3||belongType==0){
		$.ajax({
			type : "GET",
			global : false,
			async : false,
			url : "http://" + window.location.host
					+ "/mateDataManager/getRMAll?pageNum=" + pageNum + "&pageSize="
					+ pageSize+"&isStop="+isStop+"&keyword="+keyword,
			success : function(data) {
				var result = eval("(" + data + ")");
				metadatas = result["data"];
				for (var i = 0; i < metadatas.length; i++) {
					var state = metadatas[i]["state"] == "1" ? "启用" : "停用";
					var opState = metadatas[i]["state"] == "1" ? "停用" : "启用";
					var opStateVal = metadatas[i]["state"] == "1" ? "0" : "1";
					$("#list").append(
							"<tr>" + "<td class='active'>" + metadatas[i]["id"]
									+ "</td>" + "<td class='success'>"
									+ metadatas[i]["name"] + "</td>"
									+ "<td class='warning'>" + metadatas[i]["type"]
									+ "</td>" + "<td class='danger'>"
									+ metadatas[i]["author"] + "</td>"
									+ "<td class='info'>" + state + "</td>"
									+ "<td class='active'>"
									+ metadatas[i]["content"] + "</td>"
									+ "<td class='success'>资源元数据</td>" + "</tr>");
				}
				total += parseInt(result["total"]);
	
			}
	
		});
	}
	if(belongType==2||belongType==0){
		$.ajax({
			type : "GET",
			global : false,
			async : false,
			url : "http://" + window.location.host
					+ "/mateDataManager/getAllTM?pageNum=" + pageNum + "&pageSize="
					+ pageSize+"&isStop="+isStop+"&keyword="+keyword,
			success : function(data) {
				var result = eval("(" + data + ")");
				metadatas = result["data"];
				for (var i = 0; i < metadatas.length; i++) {
					var state = metadatas[i]["state"] == "1" ? "启用" : "停用";
					var opState = metadatas[i]["state"] == "1" ? "停用" : "启用";
					var opStateVal = metadatas[i]["state"] == "1" ? "0" : "1";
					$("#list").append(
							"<tr>" + "<td class='active'>" + metadatas[i]["id"]
									+ "</td>" + "<td class='success'>"
									+ metadatas[i]["name"] + "</td>"
									+ "<td class='warning'>" + metadatas[i]["type"]
									+ "</td>" + "<td class='danger'>"
									+ metadatas[i]["author"] + "</td>"
									+ "<td class='info'>" + state + "</td>"
									+ "<td class='active'>"
									+ metadatas[i]["content"] + "</td>"
									+ "<td class='success'>类型元数据</td>" + "</tr>");
				}
				total += parseInt(result["total"]);
	
			}
	
		});
	}
	pageNum = parseInt(pageNum);
	var totalPage = total % parseInt(pageSize) == 0 ? total
			/ parseInt(pageSize) : parseInt(total / parseInt(pageSize)) + 1;
	if (pageNum >= totalPage) {
		pageNum = totalPage;
	}
	$("#pageUL").empty();
	var pagehtml = '<li style="float:left;font-size: 12px;height:35px; line-height:35px;margin-right:20px">'
			+ '<div style="font-size:14px">共：'
			+ totalPage
			+ '&nbsp;页</div>'
			+ '</li>';
	if (pageNum > 1) {
		pagehtml += '<li><a href="#" onclick="load(\'' + (pageNum - 1)
				+ '\',\'' + 10 + '\')">上一页</a></li>';
	}
	for (var i = 0; i < totalPage; i++) {
		if (i >= (pageNum - 3) && i < (pageNum + 3)) {
			if (i == pageNum - 1) {
				pagehtml += '<li class="active"><a href="#" onclick="load(\''
						+ (i + 1) + '\',\'' + 10 + '\')">'
						+ (i + 1) + '</a></li>';
			} else {
				pagehtml += '<li><a href="#" onclick="load(\'' + (i + 1)
						+ '\',\'' + 10 + '\')">' + (i + 1)
						+ '</a></li>';
			}
		}
	}
	if (pageNum < totalPage) {
		pagehtml += '<li><a href="#" onclick="load(\'' + (pageNum + 1)
				+ '\',\'' + 10 + '\')">下一页</a></li>';
	}
	$("#pageUL").append(pagehtml);
}
