<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	
	$user_id = $_POST["user_id"];
	$query="UPDATE  user_info SET has_image ='1' WHERE user_id='$user_id'";
	  
		if($stmt=$Con->prepare($query))
		{
	
		if($stmt->execute())
		{
				$stmt->store_result();
				$userinfo['response']="Done";
				echo json_encode($userinfo);
			}
			else
			{
				$userinfo['response']="not Executed";
				echo json_encode($userinfo);
			}
		}
	
	else
	{
		echo"prepare failed";
	}
}
?>