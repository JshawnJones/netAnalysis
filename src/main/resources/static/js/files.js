$(function () {

    $( "#homeLink" ).click(function() { 
    	window.location.href = "/";
    });
    
    $( "#dashboard" ).click(function() { 
    	window.location.href = "/";
    });
    
    $( "#Shortcut" ).click(function() { 
    	window.location.href = "/sites";
    });
    
    $( "#Network" ).click(function() { 
    	window.location.href = "/network";
    });
    $( "#Logs" ).click(function() { 
    	window.location.href = "/logs";
    });
    $( "#Files" ).click(function() { 
    	window.location.href = "/files";
    });
    $( "#Git" ).click(function() { 
    	window.location.href = "/git";
    });
    $( "#Settings" ).click(function() { 
    	window.location.href = "/settings";
    });
    
    $( "#profileLabel" ).click(function() { 
    	window.location.href = "/profile";
    });
    
    $( "#logoutLabel" ).click(function() { 
    	window.location.href = "/logout";
    });
    
   
    
    $(".fileItem").click(function() {
    	$(".selectedfile").removeClass("selectedfile");
    	
    	$(this).addClass("selectedfile");
    	
    	
    });

    $("section").contextmenu(function(event) {
    	
    	event.preventDefault();
    	
    	var filePath = $(event.target).attr("filefolderpath");
    	$("#custom-menu").attr("filePath", filePath);
    	var fileFolderType = $(event.target).attr("filefoldertype");
    	$("#custom-menu").attr("filefoldertype", fileFolderType);
    	
    	$(".custom-menu").finish().toggle(100).
        css({
            top: event.pageY + "px",
            left: event.pageX + "px"
        });
    	
    	if($(event.target).attr('filefoldertype') == "folder"){
    		
    		$("#Download").addClass("displayNone");
    		$("#newFolder").addClass("displayNone");
    		$("#delete").removeClass("displayNone");
    		$("#fileUploadForm").removeClass("displayNone");
    		
    	} else if($(event.target).attr('filefoldertype') == "file"){
    		$("#Download").removeClass("displayNone");
    		$("#delete").removeClass("displayNone");
    		$("#newFolder").addClass("displayNone");
    		$("#fileUploadForm").addClass("displayNone");
    		
    	} else {
    		$("#Download").addClass("displayNone");
    		$("#delete").addClass("displayNone");
    		$("#newFolder").removeClass("displayNone");
    		$("#fileUploadForm").removeClass("displayNone");
    	}
    });
    
    // If the document is clicked somewhere
    $(document).bind("mousedown", function (e) {
        
        // If the clicked element is not the menu
        if (!$(e.target).parents(".custom-menu").length > 0) {
            
            // Hide it
            $(".custom-menu").hide(100);
        }
        
       
    });
    
    $(window).click( function(e){   
    	
    	
    	if(e.target.id == "ModalContent"
    		|| e.target.id == "ModalName"
			|| e.target.id == "ModalPath"
			|| e.target.id == "ModalcreationTime"
			|| e.target.id == "ModallastAccessTime"
			|| e.target.id == "ModallastModifiedTime"
			|| e.target.id == "ModalFileType"
			|| e.target.id == "ModalInfo"){
    		
    		
    	} else {
    		$("#Modal").addClass("displayNone");
    	}
    });
    
    
    // If the menu element is clicked
    $(".custom-menu li").click(function(){
        
        // This is the triggered action name
        switch($(this).attr("data-action")) {
            
            // A case for each action. Your actions here
	        case "info": 
	        	var xhttp = new XMLHttpRequest();
	    		
	    		xhttp.open("POST", "/getfiles/info", true);
	    		
	    		var formData = new FormData();
	    		
	    		var filePathfromModal = decodeURIComponent(unescape($("#custom-menu").attr("filePath")));
	    		
	    		
        		formData.append("path", filePathfromModal);
        		
        		xhttp.send(formData);
	    		
        		xhttp.onload = function(){
        			var resultObj = JSON.parse(xhttp.responseText)[0];
        			
        			var absolutePath = "";
        			var encodedAbsolutePath = "";
        			
        			if(resultObj.name == "E Drive"){
        				encodedAbsolutePath = "E:";
        			} else {
            			absolutePath = decodeURIComponent(unescape(resultObj.absolutePath).replace("E%3A", "E:").replace(/\+/g, '%20'));
            			
            			encodedAbsolutePath = absolutePath.slice(0, absolutePath.lastIndexOf("/")) + "/" + resultObj.name;
        			}

        			$("#Modal").removeClass("displayNone");
        			$("#ModalName").text(resultObj.name);
        			$("#ModalPath").text(encodedAbsolutePath);
        			$("#ModalcreationTime").text(resultObj.creationTime);
        			$("#ModallastAccessTime").text(resultObj.lastAccessTime);
        			$("#ModallastModifiedTime").text(resultObj.lastModifiedTime);
        			if(resultObj.isDirectory == "true"){
        				$("#ModalFileType").text("Folder");
        			} else {
        				$("#ModalFileType").text(resultObj.mimetype);
        			}
        			$("#ModalInfo").text(resultObj.size);
        			
        		}
        		
	        	break;
            case "download": 
            	
            	var xhttp = new XMLHttpRequest();
	    		
	    		xhttp.open("POST", "/download", true);
	    		
	    		var formData = new FormData();
	    		
	    		var filePathfromModal = decodeURIComponent(unescape($("#custom-menu").attr("filePath")));
	    		
        		formData.append("path", filePathfromModal);
        		
        		xhttp.send(formData);
            	
        		xhttp.onload = function () {
        		    var base64Data = this.response;
        		    var bstr = atob(base64Data), 
                    	n = bstr.length, 
                    	u8arr = new Uint8Array(n);
                    
	                while(n--){
	                    u8arr[n] = bstr.charCodeAt(n);
	                }
        		    var fileName = filePathfromModal.substring(filePathfromModal.lastIndexOf("/")+1, filePathfromModal.length);
        		    
        		    var file = new File([u8arr], fileName);
        		
        		    var a = document.createElement("a");
        		    document.body.appendChild(a);
        		    a.style = "display: none";
        		    url = window.URL.createObjectURL(file);
        	        a.href = url;
        	        a.download = fileName;
        	        a.click();
        	        window.URL.revokeObjectURL(url);
        		}
        		
            	break;
            case "delete": 
            	var xhttp = new XMLHttpRequest();
	    		
	    		xhttp.open("POST", "/deleteFile", true);
	    		
	    		var formData = new FormData();
	    		var filePathfromModal = decodeURIComponent(unescape($("#custom-menu").attr("filePath")));
	    		
        		formData.append("path", filePathfromModal);
        		formData.append("type", $("#custom-menu").attr("filefoldertype"));
        		xhttp.send(formData);
        		
        		xhttp.onload = function () {
        			alert("削除成功！");
        			location.reload();
        		}
            	
            	
            	break;
            case "newFoler": 
	    		var filePathfromModal = decodeURIComponent(unescape($("#custom-menu").attr("filePath")));

            	$("#fileExplorer").append("<li class='fileItem' filefoldername='' " +
						  "filefoldertype='' filefolderpath=''>"+
		    				"<div class='fileIcon fileFolderIcon' filefolderpath='' filefoldertype=''></div>"+
		    				"<div id='inputFolderNameContainer' class='fileDetails' filefolderpath='' filefoldertype=''>"+
		    					"<input id='inputFolderName' onchange='folderName()' name='name'></input>"+
		    					"<div  style='display:none;' id='folderPath' filePathfromModal='"+filePathfromModal+"'></div>"+
		    					"<div class='fileDiscription' filefolderpath='' filefoldertype=''>folder</div>"+
		    				"</div>"+
		    			"</li>");
            	
            	document.getElementById("inputFolderName").select();
            	
            	$("#inputFolderNameContainer").focusout(function(){
                		if(document.getElementById("inputFolderName").value == ""){
                			location.reload();
                		}
            		    
            	});
            	
            	break;
            	
        }
      
        // Hide it AFTER the action was triggered
        $(".custom-menu").hide(100);
      });
    
    
    
    $(".fileItem").dblclick(function() {
    	var $filetype = $(this).attr( "filefoldertype" );
    	
    	if($filetype == "folder"){
    		var $filename = $(this).attr( "filefolderpath" );

    		location.assign("/files/"+$filename);

    	}
    	
    });
    
    $("#fileUploadFileLabel").click(function() {

    	$("#fileUploadFile").click();
    	
    });

	$("#fileUploadFile").change(function(){
		var filePathfromModal = decodeURIComponent(unescape($("#custom-menu").attr("filePath")));
		
		//send ajax here
		var xhttp = new XMLHttpRequest();
		var file = document.getElementById('fileUploadFile').files[0];
		var fileName = document.getElementById('fileUploadFile').files[0].name;
		var formData = new FormData();
		
		xhttp.open("POST", "/fileupload", true);
		
		formData.append("path", filePathfromModal);
		formData.append("file", file);
		formData.append("fileName", fileName);
		
		xhttp.send(formData);
		
		xhttp.onload = function(){
			alert("アップロード成功!");
			location.reload();
		}
	});

	$("#fileUploadFileDragDrop").change(function(){
		
		var filePathfromModal = $("section").attr("filefolderpath");
		
		//send ajax here
		var xhttp = new XMLHttpRequest();
		var file = document.getElementById('fileUploadFileDragDrop').files[0];
		var fileName = document.getElementById('fileUploadFileDragDrop').files[0].name;
		var formData = new FormData();
				
		xhttp.open("POST", "/fileupload", true);
		
		formData.append("path", filePathfromModal);
		formData.append("file", file);
		formData.append("fileName", fileName);
		
		xhttp.send(formData);
		
		xhttp.onload = function(){
			alert("アップロード成功!");
			location.reload();
		}
	});
	
    $( "#profilePic" ).click(function() { 
    	var dropClasses = $( "#dropDownMenu" ).attr('class');
    	if (dropClasses.indexOf("displayNone") >= 0){
    		$( "#dropDownMenu" ).removeClass('displayNone');
    	} else {
    		$( "#dropDownMenu" ).addClass('displayNone');
    	}

    });
        

});

function folderName(){
	var newName = document.getElementById("inputFolderName").value;

	var doesNotExistFlag = true;
	$("#fileExplorer > li").find(".fileName").each(function () {
		if($(this).text() == newName){
			doesNotExistFlag = false;
		}
	});
	
	if(doesNotExistFlag){
		
		var xhttp = new XMLHttpRequest();
		
		xhttp.open("POST", "/newFolder", true);
		
		var formData = new FormData();
		
		var filePathfromModal = decodeURIComponent(unescape($("#folderPath").attr("filePathfromModal")));
		
		
		formData.append("path", filePathfromModal);
		formData.append("name", newName);
		
		xhttp.send(formData);
		
		xhttp.onload = function(){
			alert("フォルダー作成成功！");
			location.reload();
		}
		
	} else {
		alert("このフォルダー名はもう存在している！");
		location.reload();
	}
	
	
}
