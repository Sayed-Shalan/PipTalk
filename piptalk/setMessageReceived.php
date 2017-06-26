<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();

}
else
{   
	 
	 $message_ID = $_POST["message_id"];
	 $message_body = $_POST["message_body"];


	$query="INSERT INTO main_chat (m_received) VALUES ('1') WHERE (message_id ='$message_ID' AND message_body='$message_body' ) ; ";
	  
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
				$userinfo['response']="error in query execution";
				echo json_encode($userinfo);
			}
		}
	
	else
	{
		echo"prepare failed in first query";
	}
	}
	
?>