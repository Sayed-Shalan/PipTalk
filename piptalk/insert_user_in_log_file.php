<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();

}

else
{
	$date= $_POST["date"];
	$time=$_POST["time"];
	$user_id=$_POST["user_id"];
	$query="INSERT INTO log_file (user_id,time,date) VALUES ('$user_id','$time','$date')";
	  
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