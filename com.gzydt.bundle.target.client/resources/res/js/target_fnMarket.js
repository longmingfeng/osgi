window.onload=function(){
	isShow();
	getLogNum();
	resetForm();
}
//判断是显示信息界面还是注册界面
function isShow(){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/registerInfo/conn",
        success:function (data) {
        	if(data=="节点容器未注册"){
        		$("#registTarget").show();
        		$("#menu").hide();
        		$("#funList").hide();
        	}else if(data=="连接失败"){
        		$("#serverFailTarget").show();
        		$("#funList").hide();
            }else{
        		$("#registTarget").hide();
        		//连接成功显示信息
        		showBundleByTile(1);
        	}
        }
    }); 
}

//获取服务器信息日志数目
function getLogNum(){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/targetInfo/getLogNum",
        success:function (data) {
        	if(data!="{}"){
        		var obj = eval("(" + data + ")");
        		var log=obj["logs"];
        		var num=0;
        		if(obj["state"]=="连接失败"){
        			num++;
        		}
        		for (var i = 0; i < log.length; i++) {
        			num++;
        		}
        		$("#errorNum").text(num);
        	}
        }
    }); 
}

//注册节点时获取默认服务器地址
function getDefaultServerURL(){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/registerInfo/getServerURL",
        success:function (data) {
        	if(data!=null && data!=""){
        		var rgExp = /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/;
        		var ipAddress = rgExp.exec(data);
        		var port = data.substring(data.lastIndexOf(":")+1);
        		$("#editServerIp").val(ipAddress);
        		$("#editServerPort").val(port);
        	}
        }
    }); 
}

//点击注册节点
function register(){
	//节点注册表单校验
	var validateResult = $('#editorForm').validate({  
		errorPlacement:function(error,element) {
			//设置错误提示信息显示位置
			if(element[0].name == "editServerIp"){
				error.appendTo(element.parent().parent());
			}else if(element[0].name == "editServerPort"){
				
			}else{
				error.appendTo(element.parent());
			}
	   }
	}).form();
	if(!validateResult){
		//校验不通过
		return false;
	}
	//注册的服务器地址
	var serverURL = "http://" + $("#editServerIp").val()+":"+ $("#editServerPort").val();
	$.ajax({//校验服务器上targetId是否已经存在
        type:"GET",
        global:false, 
		async: false,
        url: "http://"+window.location.host+"/registerInfo/isExistTargetId?serverURL="+serverURL+"&targetId="+$("#editTargeId").val(),
        success:function (data) {
        	// false-容器节点ID不存在
        	if(data=="false"){
        		$.ajax({//将节点注册信息写入本地和服务器
        			type:"POST",
        			url: "http://"+window.location.host+"/registerInfo",
        			data: "targetName=" + $("#editTargetName").val() 
        			+"&targetId="+$("#editTargeId").val()+ "&targetOrg="
        			+ $("#editTargetOrg").val() + "&principal="
        			+ $("#editPrincipal").val() + "&phone=" + $("#editPhone").val()
        			+ "&email=" + $("#editEmail").val()
        			+ "&address=http://" + $("#editServerIp").val()+":"+ $("#editServerPort").val()+ "&descript="
        			+ $("#editDescript").val()+"&targetAddress=http://"+window.location.host,
        			success:function (data) {
        				$.ajax({
        					type:"POST",
        					async: false,
        					global:false, 
        					url: "http://" + $("#editServerIp").val()+":"+ $("#editServerPort").val()+"/registerTarget",
        					data: "targetName=" + $("#editTargetName").val() 
                			+"&targetId="+$("#editTargeId").val()+ "&targetOrg="
                			+ $("#editTargetOrg").val() + "&principal="
                			+ $("#editPrincipal").val() + "&phone=" + $("#editPhone").val()
                			+ "&email=" + $("#editEmail").val()
                			+ "&address=http://" + $("#editServerIp").val()+":"+ $("#editServerPort").val()+ "&descript="
                			+ $("#editDescript").val()+"&targetAddress=http://"+window.location.host,
        					success:function (data) {
        						if(data=="false"){
        							info("注册失败,服务器地址连接无效!");
        							$("#registTarget").show();
        							$("#funList").hide();
        						}
        					},
        					error: function(XMLHttpRequest, textStatus, errorThrown) {
        						info("注册失败,服务器地址连接无效!");
        						$("#registTarget").show();
        						$("#funList").hide();
        					}
        				}); 
        				$.ajax({//客户端、服务端targetId一致
        					type:"POST",
        					global:false, 
        					async: false,
        					url: "http://" + $("#editServerIp").val()+":"+ $("#editServerPort").val()+"/servlet/target",
        					data: "targetId="+$("#editTargeId").val(),
        					success:function (data) {
        						if(data=="false"){
        							info("注册失败,服务器地址连接无效!");
        							$("#registTarget").show();
        							$("#funList").hide();
        						}else{
        							info("注册成功");
        							showBundleByTile(1);
        							//重置表单
        							document.getElementById("editorForm").reset(); 
        							//关闭窗口
        							$('#node_info').modal('hide');
        							$("#menu").show();
        							$("#registTarget").hide();
        						}
        					},
        					error: function(XMLHttpRequest, textStatus, errorThrown) {
        						info("注册失败,服务器地址连接无效!");
        						$("#registTarget").show();
        						$("#funList").hide();
        					}
        				}); 
        			}
        		}); 
        	}else{
        		//targetId已经存在
        		info("容器节点ID已经存在!");
        		return false;
        	}
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
        	info("注册失败,服务器地址连接无效!");
    		return false;
        }
    }); 
}
//平铺展示组件
function showBundleByTile(page){
	$.ajax({
        type:"GET",
        url:"http://"+window.location.host+"/targetInfo/getApp",
        success:function (data) {
        	var dataArr = eval("("+eval("("+data+")").apps+")");
        	if(dataArr!=null){
        		//页码相关
        		var totalCount = dataArr.length;
	        	var pageSize =2;
	        	var num = (page*pageSize)>totalCount?totalCount:(page*pageSize);
        		//GO转跳
        		if(page==undefined){
        			var goValue = $('#goValu').val();
        			var reNum = /^\d*$/;
        			//输入的不是数字或者为空
        			if(!reNum.test(goValue) || goValue==""){
        				return false;
        			}
        			//输入的数值大于总页数数
        			if(parseInt(goValue)>(dataArr.length/pageSize)){
        				return false;
        			}
        			page = $('#goValu').val();
        		}
        		$("#fnContainer").empty();
	        	//数据列表字符串
	        	var str =''
	    		for(var i=(page-1)*pageSize;i<num;i++){
	    			//获取系统中所有bundle的名字
	    			var bundleName = "";
	    			for(var n = 0;n<dataArr[i].bundles.length;n++){
	    				if(n!=dataArr[i].bundles.length-1){
	    					bundleName += dataArr[i].bundles[n]["Bundle-SymbolicName"]+",";
	    				}else{
	    					bundleName += dataArr[i].bundles[n]["Bundle-SymbolicName"];
	    				}  
	    			}
	    			$.ajax({
	    				type:"GET",
	    				global:false,
	    				async: false,
	    				url: "http://"+window.location.host+"/systemInstall/info",
	    				success:function (data) {
	    					var button = null;
	    					var dataJSON = eval("("+data+")");
	    					var installValue = "{'i':'"+i+"','id':'"+dataArr[i].id+"','name':'"+dataArr[i].name+"','version':'"+dataArr[i].version+"','bundleCount':'"+dataArr[i].bundleCount+"','bundleName':'"+bundleName+"'}";
	    					if(dataJSON[dataArr[i].id] == dataArr[i].id){
	    						var buttonName =dataJSON[dataArr[i].id+"_state"];
	    						button='<button type="button" id="install'+i+'" class="btn btn-success  btn-xs installBt" disabled="disabled">'+buttonName+'</button>';
	    						if(buttonName=="安装中"){
	    							button='<button type="button" id="install'+i+'" class="btn btn-success  btn-xs installBt" disabled="disabled">'+buttonName+'<img src="'+pluginRoot+'/res/image/a.gif"/></button>';
	    							button +='<button type="button" id="uninstall'+i+'" style="margin:0 5px" class="btn btn-success  btn-xs uninstallBt" disabled="disabled">卸载</button>';
	    						}else if(buttonName=="已安装"){
	    							button +='<button type="button" id="uninstall'+i+'" style="margin:0 5px" class="btn btn-success  btn-xs uninstallBt"><input type="hidden" value="'+installValue+'"/>卸载</button>';
	    						}else if(buttonName=="安装"){
	    							button='<button type="button" id="install'+i+'" class="btn btn-success  btn-xs installBt"><input type="hidden" value="'+installValue+'"/>安装</button>'+
	    							   '<button type="button" id="uninstall'+i+'" style="margin:0 5px" class="btn btn-success  btn-xs uninstallBt" disabled="disabled">卸载</button>';
	    						}
	    					}else{
	    						button='<button type="button" id="install'+i+'" class="btn btn-success  btn-xs installBt"><input type="hidden" value="'+installValue+'"/>安装</button>'+
	    							   '<button type="button" id="uninstall'+i+'" style="margin:0 5px" class="btn btn-success  btn-xs uninstallBt" disabled="disabled">卸载</button>';
	    					}
	    					var desclength;
	    					if(dataArr[i].description.length>40){
	    						desclength=(dataArr[i].description.substring(0,40)+"...");
	    					}else{
	    						desclength=dataArr[i].description;
	    					}
    		    			str +='<div class="col-xs-8 col-sm-7 col-md-5">'+
    		    				'<div class="row thumbnail fd" style="margin-left:10px">'+
    		    				'<div class="row">'+
    		    				'<div class="col-xs-4 col-sm-4 col-md-3">'+
    		    				'<img src="'+dataArr[i].icon+'" class="img-responsive center-block" alt="..." width="80px" height="80px">'+
    		    				'</div>'+
    		    				'<div class="col-xs-4 col-sm-5 col-md-4" style="margin-left:-25px">'+
    		    				'<ul class="list-unstyled"><li class="text-success">'+dataArr[i].name+'</li> <li>版本号：'+dataArr[i].version+'</li><li>创建人：'+dataArr[i].author+'</li><li>包含组件个数：'+dataArr[i].bundleCount+'</li><li title="'+dataArr[i].description+'">描述：'+desclength+'</li></ul>'+
    		    				'</div>'+
    		    				'<div style="margin:-20px -15px 0 0;" class="col-xs-4 col-sm-3 col-md-5">'+
    		    					button+
    		    				'</div></div></div></div>'+
    		    				'<div class="col-xs-1 col-sm-1 col-md-1"></div>'
	    				},
	    				error: function(XMLHttpRequest, textStatus, errorThrown) {
	    					info("读取安装进度失败");
	    				}
	    			});
	    			
	    		}
	        	$("#fnContainer").append('<div class="row">'+str+'</div>');
	        	//显示信息
	        	$("#funList").show();
	        	createNav(page,totalCount,pageSize,"showBundleByTile");
	        	install(page);
	        	uninstall(page);
        	}else{
	        	$("#funList").show();
	        	$("#fnContainer").append('<div class="row" style="margin-left:10px">组件仓库暂时没有系统</div>');
        	}
        }
    }); 
}
//获取应用安装操作日志
function getOperateLog(){
	$.ajax({
		type:"GET",
		global:false,
		url: "http://"+window.location.host+"/systemInstall/readAppOperateLog",
		success:function (data) {
			$("#logContent").html(data);
		},
        error: function(XMLHttpRequest, textStatus, errorThrown) {
        	info("获取安装日志失败!");
        }
	});
}
//卸载应用系统
function uninstall(page){
	$(".uninstallBt").click(function(){
		//获取参数值
		var _this = $(this);
		var dataObj =_this.children("input").val();
		var systemId = eval("("+dataObj+")").id;
		var name = eval("("+dataObj+")").name;
		var bundleCount=eval("("+dataObj+")").bundleCount;
		var i = eval("("+dataObj+")").i;
		$("#uninstall"+i).html('卸载中<img src="'+pluginRoot+'/res/image/a.gif"/>');
		$("#uninstall"+i).attr("disabled","disabled");
		//获取targetid和服务器ip地址
		$.ajax({
	        type:"GET",
	        url: "http://"+window.location.host+"/targetInfo/getTargetIdServerIp",
	        success:function (data) {
	        	var obj=eval("(" + data + ")");
	        	//服务器端卸载应用
	        	$.ajax({
	                type:"POST",
	                global:false, 
	                data:"apps=["+dataObj+"]&targetId="+obj["targetId"],
	                url: obj["serverIp"]+"/servlet/appstore?opt=uninstall",
	                success:function (msgData) {
	                	var msgObj = eval("(" + msgData + ")");
	                	if(msgObj["message"] == "success"){
	            			//清除本地应用信息
	            			$.ajax({
	            				type:"GET",
	            				global:false,
	            				url: "http://"+window.location.host+"/systemInstall/uninstall?systemId="+systemId+"&name="+name+"&bundleCount="+bundleCount,
	            				success:function (data) {
	            					showBundleByTile(page);
	            					info("卸载成功!");
	            				},
	            	            error: function(XMLHttpRequest, textStatus, errorThrown) {
	            	            	info("卸载失败!");
	            	            }
	            			});
	                	}else{
	                		info("卸载失败");
	                	}
	                },
	                error: function(XMLHttpRequest, textStatus, errorThrown) {
	                	info("卸载失败");
	                }
	            }); 
	        }
	    }); 
	});
}
//安装系统
function install(page){
	$(".installBt").click(function(){
		var _this = $(this);
		//获取参数值
		var dataObj =_this.children("input").val();
		var bundle_num = eval("("+dataObj+")").bundleCount;
		var version = eval("("+dataObj+")").version;
		var systemId = eval("("+dataObj+")").id;
		var name = eval("("+dataObj+")").name;
		var bundleName = eval("("+dataObj+")").bundleName;
		var i = eval("("+dataObj+")").i;
		$("#install"+i).html('安装中<img src="'+pluginRoot+'/res/image/a.gif"/>');
		$("#install"+i).attr("disabled","disabled");
		//获取targetid和服务器ip地址
		$.ajax({
	        type:"GET",
	        url: "http://"+window.location.host+"/targetInfo/getTargetIdServerIp",
	        success:function (data) {
	        	var obj=eval("(" + data + ")");
	        	console.info(obj["serverIp"]+"/servlet/appstore");
	        	//服务器端组装应用系统
	        	$.ajax({
	                type:"POST",
	                global:false, 
	                async: false,
	                data:"apps=["+dataObj+"]&targetId="+obj["targetId"],
	                url: obj["serverIp"]+"/servlet/appstore",
	                success:function (msgData) {
	                	var msgObj = eval("(" + msgData + ")");
	                	if(msgObj["message"] == "success"){
	                		//本地安装应用系统
	                		$.ajax({
	                			type:"GET",
	                			global:false,
	                			url: "http://"+window.location.host+"/systemInstall?bundle_num="+bundle_num+"&systemId="+systemId+"&version="+version+"&name="+name+"&bundleName="+bundleName,
	                			success:function (data) {
	                				if(data!="安装"){
		                				showBundleByTile(page);
		                				getLogNum();
		                				info("安装成功!");
	                				}else{
	                					showBundleByTile(page);
	                					getLogNum();
		                				info("安装失败!");
	                				}
	                			},
	        	                error: function(XMLHttpRequest, textStatus, errorThrown) {
	        	                	info("安装失败");
	        	                }
	                		});
	                	}else{
	                		info("安装失败");
	                	}
	                },
	                error: function(XMLHttpRequest, textStatus, errorThrown) {
	                	info("安装失败");
	                }
	            }); 
	        }
	    }); 
	})
}
//重置注册表单
function resetForm(){
	$(".close").click(function(){
		document.getElementById("editorForm").reset(); 
	});
	$("#closeForm").click(function(){
		document.getElementById("editorForm").reset();
	});
}

function info(message){
    var dialog=BootstrapDialog.show({
         message: message,
         title: "提示消息",
    });
    setTimeout(function(){dialog.close()},2000);
}
