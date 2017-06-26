<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$id = $_POST["user_id"];

	$query="SELECT state,user_name FROM users WHERE id='$id' ;";
	  
		if($stmt=$Con->prepare($query))
		{
	
		if($stmt->execute())
		{
				$stmt->store_result();
			$count = $stmt->num_rows();
		
			if($count==1)
			{
				$stmt->bind_result($user_state,$userName);
				while($stmt->fetch())
				{
					$temp = array ('user_state'=>$user_state,'user_name'=>$userName);
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