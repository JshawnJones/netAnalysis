var stompClient = null;

$( document ).ready(function() {
	connect();

});

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);

    var userIP = $("#currentUserIP").text();
    
    stompClient.connect({

    }, function () {
    	
    	var websocketUrl = socket.url.replace("http://", "");
    	var transportUrl = socket._transport.url.replace("ws://", "");
    	var parsedUrl = transportUrl.replace(websocketUrl, "");
    	
    	var sessionid = parsedUrl.split("/")[2];
    	
    	stompClient.subscribe('/network/cpuresults' + sessionid, function (result) {

    		var jsonresult = JSON.parse(result.body);
    		var jqueryjsonresult = $.parseJSON(result.body);
    		
    		
			//Main CPU
			var cpuPercentage = jsonresult.CPUload / 100;
			var lineheightfromTop = Math.round(cpuPercentage * $("#cpuoutput").height());
			var lineheight = $("#cpuoutput").height() - lineheightfromTop;
			
			var prePoints = $("#cpuLine").attr("points");
			var prePointsArray = prePoints.split(" ");
			
			var newPoints = "00,295 00,"+lineheight+" ";
			for (var $i = 1; $i < prePointsArray.length -1; $i++) {
				  
				var value = prePointsArray[$i].split(",")[1];
				var test = parseInt(prePointsArray[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 400){
					newPoints = newPoints.concat(position+","+value+" ");
				}
		
			}
			newPoints = newPoints.concat("400,295");
			$("#cpuLine").attr("points",newPoints);
			
			$("#cpuload").text("CPU Load: " + jsonresult.CPUload + "%");
			
			//Core 1
			var core1Percentage = jsonresult.Core1 / 100;
			var lineheightfromTop1 = Math.round(core1Percentage * $("#core1output").height());
			var lineheight1 = $("#core1output").height() - lineheightfromTop1;
			
			var prePoints1 = $("#cpuLine1").attr("points");
			var prePointsArray1 = prePoints1.split(" ");
			
			var newPoints1 = "00,125 00,"+lineheight1+" ";
			for (var $i = 1; $i < prePointsArray1.length -1; $i++) {
				  
				var value = prePointsArray1[$i].split(",")[1];
				var test = parseInt(prePointsArray1[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints1 = newPoints1.concat(position+","+value+" ");
				}
		
			}
			newPoints1 = newPoints1.concat("200,125");
			$("#cpuLine1").attr("points",newPoints1);
			
			$("#cpuCore1load").text("Core 1: " + jsonresult.Core1 + "%");
			
			//Core 2
			var core2Percentage = jsonresult.Core2 / 100;
			var lineheightfromTop2 = Math.round(core2Percentage * $("#core2output").height());
			var lineheight2 = $("#core2output").height() - lineheightfromTop2;
			
			var prePoints2 = $("#cpuLine2").attr("points");
			var prePointsArray2 = prePoints2.split(" ");
			
			var newPoints2 = "00,125 00,"+lineheight2+" ";
			for (var $i = 1; $i < prePointsArray2.length -1; $i++) {
				  
				var value = prePointsArray2[$i].split(",")[1];
				var test = parseInt(prePointsArray2[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints2 = newPoints2.concat(position+","+value+" ");
				}
		
			}
			newPoints2 = newPoints2.concat("200,125");
			$("#cpuLine2").attr("points",newPoints2);
			
			$("#cpuCore2load").text("Core 2: " + jsonresult.Core2 + "%");
			
			//Core 3
			var core3Percentage = jsonresult.Core3 / 100;
			var lineheightfromTop3 = Math.round(core3Percentage * $("#core3output").height());
			var lineheight3 = $("#core3output").height() - lineheightfromTop3;
			
			var prePoints3 = $("#cpuLine3").attr("points");
			var prePointsArray3 = prePoints3.split(" ");
			
			var newPoints3 = "00,125 00,"+lineheight3+" ";
			for (var $i = 1; $i < prePointsArray3.length -1; $i++) {
				  
				var value = prePointsArray3[$i].split(",")[1];
				var test = parseInt(prePointsArray3[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints3 = newPoints3.concat(position+","+value+" ");
				}
		
			}
			newPoints3 = newPoints3.concat("200,125");
			$("#cpuLine3").attr("points",newPoints3);
			
			$("#cpuCore3load").text("Core 3: " + jsonresult.Core3 + "%");
			
			//Core 4
			var core4Percentage = jsonresult.Core4 / 100;
			var lineheightfromTop4 = Math.round(core4Percentage * $("#core4output").height());
			var lineheight4 = $("#core4output").height() - lineheightfromTop4;
			
			var prePoints4 = $("#cpuLine4").attr("points");
			var prePointsArray4 = prePoints4.split(" ");
			
			var newPoints4 = "00,125 00,"+lineheight4+" ";
			for (var $i = 1; $i < prePointsArray4.length -1; $i++) {
				  
				var value = prePointsArray4[$i].split(",")[1];
				var test = parseInt(prePointsArray4[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints4 = newPoints4.concat(position+","+value+" ");
				}
		
			}
			newPoints4 = newPoints4.concat("200,125");
			$("#cpuLine4").attr("points",newPoints4);
			
			$("#cpuCore4load").text("Core 4: " + jsonresult.Core4 + "%");
			
			
			//Core 5
			var core5Percentage = jsonresult.Core5 / 100;
			var lineheightfromTop5 = Math.round(core5Percentage * $("#core5output").height());
			var lineheight5 = $("#core5output").height() - lineheightfromTop5;
			
			var prePoints5 = $("#cpuLine5").attr("points");
			var prePointsArray5 = prePoints5.split(" ");
			
			var newPoints5 = "00,125 00,"+lineheight5+" ";
			for (var $i = 1; $i < prePointsArray5.length -1; $i++) {
				  
				var value = prePointsArray5[$i].split(",")[1];
				var test = parseInt(prePointsArray5[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints5 = newPoints5.concat(position+","+value+" ");
				}
		
			}
			newPoints5 = newPoints5.concat("200,125");
			$("#cpuLine5").attr("points",newPoints5);
			
			$("#cpuCore5load").text("Core 5: " + jsonresult.Core5 + "%");
			
			//Core 6
			var core6Percentage = jsonresult.Core6 / 100;
			var lineheightfromTop6 = Math.round(core6Percentage * $("#core6output").height());
			var lineheight6 = $("#core6output").height() - lineheightfromTop6;
			
			var prePoints6 = $("#cpuLine6").attr("points");
			var prePointsArray6 = prePoints6.split(" ");
			
			var newPoints6 = "00,125 00,"+lineheight6+" ";
			for (var $i = 1; $i < prePointsArray6.length -1; $i++) {
				  
				var value = prePointsArray6[$i].split(",")[1];
				var test = parseInt(prePointsArray6[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints6 = newPoints6.concat(position+","+value+" ");
				}
		
			}
			newPoints6 = newPoints6.concat("200,125");
			$("#cpuLine6").attr("points",newPoints6);
			
			$("#cpuCore6load").text("Core 6: " + jsonresult.Core6 + "%");
			
			//Core 7
			var core7Percentage = jsonresult.Core7 / 100;
			var lineheightfromTop7 = Math.round(core7Percentage * $("#core7output").height());
			var lineheight7 = $("#core7output").height() - lineheightfromTop7;
			
			var prePoints7 = $("#cpuLine7").attr("points");
			var prePointsArray7 = prePoints7.split(" ");
			
			var newPoints7 = "00,125 00,"+lineheight7+" ";
			for (var $i = 1; $i < prePointsArray7.length -1; $i++) {
				  
				var value = prePointsArray7[$i].split(",")[1];
				var test = parseInt(prePointsArray7[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints7 = newPoints7.concat(position+","+value+" ");
				}
		
			}
			newPoints7 = newPoints7.concat("200,125");
			$("#cpuLine7").attr("points",newPoints7);
			
			$("#cpuCore7load").text("Core 7: " + jsonresult.Core7 + "%");
			
			//Core 8
			var core8Percentage = jsonresult.Core8 / 100;
			var lineheightfromTop8 = Math.round(core8Percentage * $("#core8output").height());
			var lineheight8 = $("#core8output").height() - lineheightfromTop8;
			
			var prePoints8 = $("#cpuLine8").attr("points");
			var prePointsArray8 = prePoints8.split(" ");
			
			var newPoints8 = "00,125 00,"+lineheight8+" ";
			for (var $i = 1; $i < prePointsArray8.length -1; $i++) {
				  
				var value = prePointsArray8[$i].split(",")[1];
				var test = parseInt(prePointsArray8[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= 200 + 20){
					newPoints8 = newPoints8.concat(position+","+value+" ");
				}
		
			}
			newPoints8 = newPoints8.concat("200,125");
			$("#cpuLine8").attr("points",newPoints8);
			
			$("#cpuCore8load").text("Core 8: " + jsonresult.Core8 + "%");
			
			//physical memory
			$("#phymemload").text("Physical Memory: " + jsonresult.PhysicalMemory);
			
			var memUsed = jsonresult.PhysicalMemory.replace(" GiB", "").split("/")[0];
			var memAvailable = jsonresult.PhysicalMemory.replace(" GiB", "").split("/")[1].replace(" GiB", "");

			//memory usage percentage
			var phyMemPercentage = (memUsed / memAvailable).toFixed(2);
			
			var prePointsPhyMem = $("#physicalMemLine").attr("points");
			var prePointsArrayPhyMem = prePointsPhyMem.split(" ");
			
			var lineheightfromTopPhyMem = Math.round(phyMemPercentage * $("#physicalMemOutput").height());
			var lineheightPhyMem = $("#physicalMemOutput").height() - lineheightfromTopPhyMem;
			
			var phyMemWidth = Math.round($("#physicalMemOutput").width());
			
			//points to display
			var newPointsPhyMem = "00,125 00,"+lineheightPhyMem+" ";
			var lastPoint = "";
			for (var $i = 1; $i < prePointsArrayPhyMem.length -1; $i++) {
				  
				var value = prePointsArrayPhyMem[$i].split(",")[1];
				var test = parseInt(prePointsArrayPhyMem[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= phyMemWidth +20){
					newPointsPhyMem = newPointsPhyMem.concat(position+","+value+" ");
				}
				lastPoint = position;
			}
			lastPoint = lastPoint + 20;
			
			newPointsPhyMem = newPointsPhyMem.concat(lastPoint+",125");
			
			$("#physicalMemLine").attr("points",newPointsPhyMem);
			
			//virtual memory
			$("#virmemload").text("Virtual Memory: " + jsonresult.VirtualMemory);
			
			var virmemUsed = jsonresult.VirtualMemory.replace(" GiB", "").split("/")[0];
			var virmemAvailable = jsonresult.VirtualMemory.replace(" GiB", "").split("/")[1].replace(" GiB", "");

			//memory usage percentage
			var virMemPercentage = (virmemUsed / virmemAvailable).toFixed(2);
			
			var prePointsVirMem = $("#virtualMemLine").attr("points");
			var prePointsArrayVirMem = prePointsVirMem.split(" ");
			
			var lineheightfromTopVirMem = Math.round(virMemPercentage * $("#virtualMemOutput").height());
			var lineheightVirMem = $("#virtualMemOutput").height() - lineheightfromTopVirMem;
			
			var phyMemWidth = Math.round($("#physicalMemOutput").width());
			
			//points to display
			var newPointsVirMem = "00,125 00,"+lineheightVirMem+" ";
			var virlastPoint = "";
			for (var $i = 1; $i < prePointsArrayVirMem.length -1; $i++) {
				  
				var value = prePointsArrayVirMem[$i].split(",")[1];
				var test = parseInt(prePointsArrayVirMem[$i].split(",")[0].replace(" ", ""));
				var position = test + 20;
				
				if(position <= phyMemWidth + 20){
					newPointsVirMem = newPointsVirMem.concat(position+","+value+" ");
				}
				virlastPoint = position;
			}
			virlastPoint = virlastPoint + 20;
			
			newPointsVirMem = newPointsVirMem.concat(virlastPoint+",125");
			
			$("#virtualMemLine").attr("points",newPointsVirMem);
			
			$("#storageList").empty();
			for (var $j = 1; $j <= parseInt(jsonresult.NumberofDisks); $j++) {
				
				$("#storageList").append("<li class='storageItem'>"+
		        "<div id='storageName"+$j+"' class='storageName'>Disk: </div>"+
		        "<div id='storageUsage"+$j+"' class='storageUsage'>Storage Available: </div>"+
		        "<svg id='storageUsageGraph"+$j+"' class='storageUsageGraph' width='100%' height='65px'>"+
				  "<g>"+
				    "<rect fill='#252830' width='92%' height='25'></rect>"+
				    "<rect fill='#0074d9' id='storageUsed"+$j+"' width='0%' height='25'></rect>"+
				  "</g>"+
				"</svg>"+
				"</li>");
				
				$("#storageName"+$j).text("Disk: " + jqueryjsonresult["diskName"+$j]);
				
				var storageStr = "Storage Available: " + jqueryjsonresult["diskUsable"+$j] +
				" / "+jqueryjsonresult["diskTotal"+$j] + " (" +
				parseFloat(jqueryjsonresult["diskPercentage"+$j]).toFixed(2)+ "%)";
				$("#storageUsage"+$j).text(storageStr);
				
				var usedPercent = 100 - Math.round(parseInt(jqueryjsonresult["diskPercentage"+$j]).toFixed(2));
				$("#storageUsed"+$j).attr("width",usedPercent + "%");
			}
			
    	});
    	
      stompClient.subscribe('/network/ipresults' + sessionid, function (result) {

    	  if(!result.body.includes("user")){
    		  
	    	  $("#dashboard_main").append(" <li id='"+result.body+"' class='dashboard_machines fail'>" +
	    	  		"<ul class='dashboard_details'>" +
	    	  		"<li class='hostname'>" +
	    	  		"<div class='hostname_title'>Hostname:　</div>" +
	    	  		"<a href='#' class='host_data_name'></a>" + 
	    	  		"</li>" +
	    	  		"<li class='status'>" +
	                "<div class='status_title'>ステータス:　</div>" +
	                "<div class='host_data_signal OFFLINE'>OFFLINE</div>" +
	                "</li>" +
	                "<li class='address'>" +
	                "<div class='address_title'>Address:　</div>" + 
	                "<div class='host_data_data'>"+result.body+"</div>" + 
	                "</li>" +
	    	  		"</ul>" +
	    	  		"</li>");
    	  } else {
    		  var outputsplit = result.body.split("; user: ");
    		  
    		  
	    	  $("#dashboard_main").append(" <li id='"+outputsplit[0]+"' class='dashboard_machines'>" +
		    	  		"<ul class='dashboard_details'>" +
		    	  		"<li class='hostname'>" +
		    	  		"<div class='hostname_title'>Hostname:　"+outputsplit[1]+"</div>" +
		    	  		"<a href='#' class='host_data_name'></a>" + 
		    	  		"</li>" +
		    	  		"<li class='status'>" +
		                "<div class='status_title'>ステータス:　</div>" +
		                "<div class='host_data_signal OK'>OK</div>" +
		                "</li>" +
		                "<li class='address'>" +
		                "<div class='address_title'>Address:　</div>" + 
		                "<div class='host_data_data'>"+outputsplit[0]+"</div>" + 
		                "</li>" +
		    	  		"</ul>" +
		    	  		"</li>");
    	  }
    	  
      });
    });
    

}

function sendReadySignal() {
    $("#dashboard_main").empty();
	stompClient.send("/app/status", {}, JSON.stringify({'status': "OK"}));
}

function sendServerReadySignal() {
    
	stompClient.send("/app/server", {}, JSON.stringify({'status': "OK"}));
}

function sendServerStopSignal() {
    
	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $( "#send" ).click(function() { sendReadySignal(); });
    
    $( "#netsend" ).click(function() { 
    	
    	
		$("#netdisconnect").removeAttr("disabled");
		$("#netdisconnect").removeClass("disabled");
		$("#netdisconnect").removeClass("displayNone");
		$("#netdisconnect").addClass("enabled");
		
		
		$( "#netsend" ).attr("disabled", true);
		$( "#netsend" ).removeClass("send");
		$( "#netsend" ).addClass("disabled");
		$( "#netsend" ).addClass("displayNone");
		
		sendServerReadySignal(); 
	});
	
	$( "#netdisconnect" ).click(function() {
		$("#netdisconnect").addClass("displayNone");
		$("#netdisconnect").addClass("disabled");
		$("#netdisconnect").removeClass("enabled");
		$("#netdisconnect").attr("disabled", true);
		
		$( "#netsend" ).removeAttr("disabled");
		$( "#netsend" ).addClass("send");
		$( "#netsend" ).removeClass("disabled");
		$( "#netsend" ).removeClass("displayNone");
		
		sendServerStopSignal();

	});
    
});


$(function () {

    $( "#profilePic" ).click(function() { 
    	var dropClasses = $( "#dropDownMenu" ).attr('class');
    	if (dropClasses.indexOf("displayNone") >= 0){
    		$( "#dropDownMenu" ).removeClass('displayNone');
    	} else {
    		$( "#dropDownMenu" ).addClass('displayNone');
    	}

    });
    
});

$(function () {

    $( "#homeLink" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/";
    });
    
    $( "#dashboard" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/";
    });
    
    $( "#Shortcut" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/sites";
    });
    
    $( "#Network" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/network";
    });
    $( "#Logs" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/logs";
    });
    $( "#Files" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/files";
    });
    $( "#Git" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/git";
    });
    $( "#Settings" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/settings";
    });
    
    $( "#profileLabel" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/profile";
    });
    
    $( "#logoutLabel" ).click(function() { 
    	stompClient.send("/app/server", {}, JSON.stringify({'status': "STOP"}));
    	window.location.href = "/logout";
    });
});



function dataURLtoFile(dataurl, filename) {
	 
    var arr = dataurl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]), 
        n = bstr.length, 
        u8arr = new Uint8Array(n);
        
    while(n--){
        u8arr[n] = bstr.charCodeAt(n);
    }
    
    return new File([u8arr], filename, {type:mime});
}

$(function() {
	
	$("#fileUploadFile").change(function() {
		$( "#cropOverlay" ).remove();
		
		var file = document.getElementById('fileUploadFile').files[0];
		$( "#profilePicPreview" ).removeClass('displayNone');
		$( "#profilePicPreview" ).addClass('profilePicPreview');
		
		$( "#cropContainer" ).removeClass('displayNone');
		$( "#cropContainer" ).addClass('cropContainer');
		
		$( "#profilePiCrop" ).removeClass('displayNone');
		$( "#profilePiCrop" ).addClass('profilePiCrop');
		
		var reader = new FileReader();
		
		reader.onload = function(e) {
			
		      var fileHight;
		      var fileWidth;
		      var image = new Image();
		      image.src = e.target.result;
		      image.onload = function() {
		    	  fileWidth = image.width;
		    	  fileHight = image.height;
		      };
			
		      //load picture				
		      $('#profilePiCrop').on("load", function() {
			      var c = document.getElementById("profilePicPreview");
			      var ctx = c.getContext("2d");
			      
			      var cropHeight = document.getElementById('profilePiCrop').clientHeight;
			      var cropWidth = document.getElementById('profilePiCrop').clientWidth;
			      
			      var widthRatio = fileWidth / cropWidth;
			      var heightRatio = fileHight / cropHeight;
			      
			      var swidth = widthRatio * 100;
			      var sheight = heightRatio * 100;
			       
			      ctx.clearRect(0, 0, 1000, 1000);
			      c.height = 1000;
			      c.width = 1000;
			      
			      ctx.drawImage(document.getElementById('profilePiCrop'), 0, 0, swidth, sheight, 0, 0, 1000, 1000);
		    	  
		      }).attr('src', e.target.result);
		      
		      $('#profilePiCrop').css({
		    	  "filter": "brightness(50%)"
		      });
		      
		      $( "#icContainer" ).append( "<div id='cropOverlay'></div>" );
		      
		      $( "#cropOverlay" ).css({
		    	  "position": "absolute", 
		    	  "z-index": "999",
		    	  "border": "solid 2px rgba(201, 36, 55, 0.5)",
		    	  "cursor": "move", 
		    	  "top": "0px",
		    	  "left": "0px", 
		    	  "height":"100px",
		    	  "width": "100px",
		    	  "backdrop-filter": "brightness(200%)"
		    	});
		      
		    //jquery ui
			$( "#cropOverlay" ).addClass('draggable');
			$( "#cropOverlay" ).addClass('resizeable');
		      
			$(".resizeable").resizable({
				containment: "#cropContainer",
				aspectRatio: 1 /1 ,
				handles: "n, e, s, w",
				resize: function( event, ui ) {

					var c = document.getElementById("profilePicPreview");
				    var ctx = c.getContext("2d");
    
				    var cropHeight = document.getElementById('profilePiCrop').clientHeight;
				    var cropWidth = document.getElementById('profilePiCrop').clientWidth;
				      
				    var widthRatio = fileWidth / cropWidth;
				    var heightRatio = fileHight / cropHeight;
				    
				    var swidth = widthRatio * ui.size.width;
				    var sheight = heightRatio * ui.size.height;
				    
				    var sX = widthRatio * ui.position.left;
				    var sY = heightRatio * ui.position.top;
				    
				    ctx.clearRect(0, 0, 1000, 1000);
				    c.height = 1000;
				    c.width = 1000;
				    
				    ctx.drawImage(document.getElementById('profilePiCrop'), sX, sY, swidth, sheight, 0, 0, 1000, 1000);
				}
			});
		      
			$(".draggable").draggable({
				containment: "#cropContainer",
				drag: function( event, ui ) {
					var c = document.getElementById("profilePicPreview");
				    var ctx = c.getContext("2d");
				    
				    var cropHeight = document.getElementById('profilePiCrop').clientHeight;
				    var cropWidth = document.getElementById('profilePiCrop').clientWidth;
				      
				    var widthRatio = fileWidth / cropWidth;
				    var heightRatio = fileHight / cropHeight;
				    
				    var swidth = widthRatio * $("#cropOverlay").width();
				    var sheight = heightRatio * $("#cropOverlay").height();
				    
				    var sX = widthRatio * ui.position.left;
				    var sY = heightRatio * ui.position.top;
				    
				    ctx.clearRect(0, 0, 1000, 1000);
				    c.height = 1000;
				    c.width = 1000;
				    
				    ctx.drawImage(document.getElementById('profilePiCrop'), sX, sY, swidth, sheight, 0, 0, 1000, 1000);

				}
			});

		}
		// convert to base64 string    
		reader.readAsDataURL(file); 
		
	});
	
    $(window).click( function(e){   	
    	
    	if(e.target.id == "profileMenuPic" ){
    		
    		$( "#uploadModal" ).removeClass('displayNone');
    		
    	}else if (e.target.id == "fileUploadForm" 
    		|| e.target.id == "uploadModalContent" 
    		|| e.target.id == "fileUploadFile"
    		|| e.target.id == "cropContainer"
    		|| e.target.id == "profilePicPreview"
    		|| e.target.id == "profilePiCrop"
    		|| e.target.id == "cropOverlay"
    		|| e.target.id == "icContainer"
    		|| e.target.className.includes("ui-resizable-handle")	
    		){
    		
    	}else if (e.target.id == "fileUploadSubmit"){
    		//send ajax here
    		var xhttp = new XMLHttpRequest();
    		
    		xhttp.open("POST", "/profile/fileupload", true);
    		
    		var file = document.getElementById('fileUploadFile').files[0];
    		
    		var file_size = document.getElementById('fileUploadFile').files[0].size;
    		const converted_filesize = Math.round((file_size / 1024));
    		if (converted_filesize >= 10240) { 
    			
    			alert("ファイルは10 mb超えてる！");
    			
    		} else if (!file.type.includes("image")){
    			
    			alert("画像ファイルではない！");
    			
    		}else {
    			var canvas = document.getElementById('profilePicPreview');
    			
    			var extension = canvas.toDataURL().split(';base64,')[0].replace("data:image/", "");
    			
    			var newfile = dataURLtoFile(canvas.toDataURL(),'upload.'+ extension);
    			var formData = new FormData();
        		formData.append("file", newfile);
        		
        		xhttp.send(formData);
        		
        		xhttp.onload = function(){
        			alert("アップロード成功!");
        			
        			$( "#cropContainer" ).addClass('displayNone');
        			$( "#profilePiCrop" ).addClass('displayNone');
            		$( "#profilePicPreview" ).addClass('displayNone');
            		$( "#profilePicPreview" ).removeClass('profilePicPreview');
        			$( "#dropDownMenu" ).addClass('displayNone');
        			$( "#cropOverlay" ).addClass('displayNone');
        			location.reload();
        		}

    		}

    	}else{
    		$( "#uploadModal" ).addClass('displayNone');
    	}
    	
    });
	
});

function profileName(){
	
		var newName = document.getElementById('usernameP').value;
		var xhttp = new XMLHttpRequest();
		
		xhttp.open("POST", "/profile/profileName", true);
		var formData = new FormData();
		formData.append("name", newName);
		
		xhttp.send(formData);
		
		xhttp.onload = function(){
			alert("名前変更成功！");
			location.reload();
		}
}

function showAddAccount(){
	if($( "#addAccount" ).hasClass( "addAccount" )){
		$( "#addAccount" ).removeClass('addAccount');
		$( "#addAccount" ).addClass('closeaddAccount');
		$( "#userRegistration" ).removeClass('displayNone');
		$( "#emailRegistration" ).removeClass('displayNone');
		$( "#sendButton" ).removeClass('displayNone');
	} else {
		$( "#addAccount" ).addClass('addAccount');
		$( "#addAccount" ).removeClass('closeaddAccount');
		$( "#userRegistration" ).addClass('displayNone');
		$( "#emailRegistration" ).addClass('displayNone');
		$( "#sendButton" ).addClass('displayNone');
	}
}

function addEmailAccount(){
	
	var email = document.getElementById('emailRegistration').value;
	var xhttp = new XMLHttpRequest();

	var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	var result = regex.test(email);
	
	if(result){
		xhttp.open("POST", "/profile/addByEmail", true);
		var formData = new FormData();
		formData.append("email", email);
		
		xhttp.send(formData);
		
		xhttp.onload = function(){
			alert("メール送信!");
			location.reload();
		}
	} else {
		alert("無効メール入力!");
	}

}
