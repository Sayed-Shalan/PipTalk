<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$user_id = $_POST["user_id"];
	
	  
		if($stmt=$Con->prepare("UPDATE  users SET state ='Inactive'  WHERE id=?"))
		{
			$stmt->bind_param('i',$user_id);
	
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