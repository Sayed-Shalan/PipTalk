<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$username = $_POST["username"];
	
	  
		if($stmt=$Con->prepare("SELECT * FROM users WHERE user_name=?"))
		{
			$stmt->bind_param('s',$username);
			
		if($stmt->execute())
		{
				$stmt->store_result();
			$count = $stmt->num_rows();
		
			if($count==1)
			{
			$userinfo['response']="exist";
				echo json_encode($userinfo);
					
			}
			else
			{
				$userinfo['response']="not_exist";
				echo json_encode($userinfo);
			}
		}
		else
		{
			echo"execute failed";
		}
	}
	else
	{
		echo"prepare failed";
	}
}
?>