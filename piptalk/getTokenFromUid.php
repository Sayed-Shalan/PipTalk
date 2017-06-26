<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$user_id = $_POST["user_id"];

	$query="SELECT user_token FROM users WHERE id='$user_id'";
	  
		if($stmt=$Con->prepare($query))
		{
	
		if($stmt->execute())
		{
			$stmt->store_result();
			$count = $stmt->num_rows();
		
			if($count==1)
			{
				$stmt->bind_result($user_token);
				while($stmt->fetch())
				{
					$temp = array ('user_token'=>$user_token);
					$userinfo = array('response'=>'success', 'user'=>$temp);
					echo json_encode($userinfo);
				}
			}
			else
			{
				$userinfo['response']="not found";
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