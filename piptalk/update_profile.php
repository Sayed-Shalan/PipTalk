<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$status = $_POST["status"];
	$language=$_POST["lang"];
	$gender=$_POST["gender"];
	$phone=$_POST["phone"];
	$birth_date=$_POST["birth_date"];
	$user_id = $_POST["user_id"];
	
	  
		if($stmt=$Con->prepare("UPDATE  user_info SET status =? ,native_language=?, gender=? , phone_number=? , date_of_birth=? WHERE user_id=?"))
		{
			$stmt->bind_param('sssssi',$status,$language,$gender,$phone,$birth_date,$user_id);
	
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