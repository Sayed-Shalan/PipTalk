<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();

}

else
{
	
	$userToken = $_POST["token"];
	$userState = $_POST["user_state"];
	$userpass = $_POST["pass"];
    $username = $_POST["username"];
	$userlang = $_POST["lang"];
	$usergender= $_POST["gender"];
	$has_image=$_POST["has_image"];
	$birthday=$_POST["birthday"];
	 
	 
     
	
	
	 
		if($stmt = $Con->prepare("INSERT INTO users (user_token,password,user_name,state,admin) VALUES (?,?,?,?,'0') ;" ))
		{
	       $stmt->bind_param('ssss',$userToken ,$userpass,$username,$userState );
		if($stmt->execute())
		{
			$stmt->store_result();
			
	if($stmt=$Con->prepare("INSERT INTO user_info (native_language,gender,has_image,status,phone_number,date_of_birth) VALUES (?,?,?,'this is my status','Empty',?)"))
	{
		$stmt->bind_param('ssss',$userlang  ,$usergender,$has_image,$birthday);
		if($stmt->execute()){
			$stmt->store_result();
			
			if($stmt=$Con->prepare("SELECT id FROM users WHERE user_name=? AND password=? ")){
				$stmt->bind_param('ss',$username,$userpass);
				if($stmt->execute()){
					$stmt->store_result();
					$stmt->bind_result($userid);
					$stmt->fetch();
					
				$userinfo=array('response'=>'done','user_id'=>$userid);
				echo json_encode($userinfo);
				}else{
					echo 'execute failed for query3';
				}
			}else{
				echo 'prepare failed for query3';
			}
				
		}
		else{
			$userinfo['response']="error in second query execution";
		}
	}
	else{
		echo"prepare failed in second query";
	}
			}
			else
			{
				$userinfo['response']="error in first query execution";
				echo json_encode($userinfo);
			}
		}
	
	else
	{
		echo"prepare failed in first query";
	}
	}
	
?>