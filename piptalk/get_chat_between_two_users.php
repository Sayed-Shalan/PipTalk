<?php
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
}else{
	$user1_id=$_POST["user1_id"];
	$user2_id=$_POST["user2_id"];
	$query="SELECT * FROM main_chat WHERE (sender_id='$user1_id' AND receiver_id='$user2_id') OR (sender_id='$user2_id' AND receiver_id='$user1_id') ORDER BY message_id ASC ;";
	if($stmt=$Con->prepare($query)){
		if($stmt->execute()){
			$data=array();
			$stmt->store_result();
			$stmt->bind_result($message_id,$sender_id,$receiver_id,$message_body,$msg_lang_code,$time,$date,$m_sent,$m_received);
			while($stmt->fetch()){
				$temp=array('message_id'=>$message_id,
				'message_body'=>$message_body,
				'msg_lang_code'=>$msg_lang_code,
				'time'=>$time,
				'date'=>$date,
				'sender_id'=>$sender_id,
																'receiver_id'=>$receiver_id,
																'm_sent'=>$m_sent,
																'm_received'=>$m_received);
				                                                 array_push($data,$temp);
			}
			$temp2=array('response'=>'success','user_messages'=>$data);
			echo json_encode($temp2);
		}
	}
}

?>