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
   
    $( "#gitTitle" ).click(function() { 
    	window.location.href = "/git";
    });
    
    $( "#profilePic" ).click(function() { 
    	var dropClasses = $( "#dropDownMenu" ).attr('class');
    	if (dropClasses.indexOf("displayNone") >= 0){
    		$( "#dropDownMenu" ).removeClass('displayNone');
    	} else {
    		$( "#dropDownMenu" ).addClass('displayNone');
    	}

    });
    
    $(".repoTitle").click(function() {
    	var $repoTitle = $(this).attr('id');
    	var $repoHeadBranch = $(this).attr('branch');
    	
		location.assign("/git/code/" + $repoHeadBranch + "/" + $repoTitle);

		
    });
	
    $(".gitListItem").click(function() {

		var $gitPath = $(this).attr("gitpath");
		var $gitbranch = $(".branchesList").val();
		
		window.location.href = "/git/code/" + $gitbranch + "/" + $gitPath;
    	
    });
    
    
    $(".gitCodeLink").click(function() { 
    	
    	var $gitValue = $(this).attr("gitValue");
    	var $gitbranch = $(this).attr('branch');
    	
    	
    	window.location.href = "/git/code/" + $gitbranch + "/" + $gitValue;
    });
    
    $(".gitCommitsLink").click(function() { 
    	var $repoName = $(this).attr("repoName");
    	var $gitbranch = $(this).attr('branch');
    	
    	window.location.href = "/git/commits/" + $gitbranch + "/" +  $repoName;
    });
    
    $("#branchesList").change(function(){
    	var　$branch = $(this).val();
    	var $gitRepoName = $("a.mainlink").text();
    	
    	window.location.href = "/git/code/" + $branch + "/" + $gitRepoName;
    });
    
    $("#commitbranchesList").change(function(){
    	var　$branch = $(this).val();
    	var $gitRepoName = $("div.mainlink").text();
    	
    	window.location.href = "/git/commits/" + $branch + "/" + $gitRepoName;
    });

    $(".pullRequestsLink").click(function(){
    	var $repoName = $(this).attr("repoName");
    	var $gitbranch = $(this).attr('branch');
    	
    	window.location.href = "/git/compareCommits/" + $repoName;
    });
    
    $("#compareBranches").click(function(){
    	
    	var branch1 = document.getElementById('branchesList1').value;
    	var branch2 = document.getElementById('branchesList2').value;
    	var $repoName = $("#branchesList1").attr("repoName");
    	
    	
    	var xhttp = new XMLHttpRequest();
    	xhttp.open("POST", "/git/getCompareCommits/" + $repoName, true);
    	var formData = new FormData();
		formData.append("branch1", branch1);
		formData.append("branch2", branch2);
		
		xhttp.send(formData);
		xhttp.onload = function(){
			
			$(".commitDiffItem").remove();
			
			var commitdiffObj = JSON.parse(xhttp.responseText)[0];
			
			$.each(commitdiffObj, function(key,value) {
				$("#commitsCompareTable").append("<tr class='commitDiffItem' ><td class='gitCommitItem'>" + value.change + "</td>"
						+"<td class='gitCommitItem'>" + value.path + "</td></tr>");
			});
			
		}
    });
    
});
