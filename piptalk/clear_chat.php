<?php
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
}else{
	$user1_id=$_POST["user1_id"];
	$user2_id=$_POST["user2_id"];
	$divider=$_POST["divider"];

	$query="DELETE FROM main_chat WHERE ((sender_id='$user1_id' AND receiver_id='$user2_id') OR (sender_id='$user2_id' AND receiver_id='$user1_id') ) AND message_id <= '$divider' ;";
	if($stmt=$Con->prepare($query)){
		if($stmt->execute()){
			$stmt->store_result();
			$temp2=array('response'=>'success');
			echo json_encode($temp2);
		}else{
			echo 'execute failed';
		}
	}else{
		echo 'prepare failed';
	}
}

?>