<?php
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
}else{
	$query="SELECT MAX(message_id) FROM main_chat ;";
	if($stmt=$Con->prepare($query)){
		if($stmt->execute()){
			$data=array();
			$stmt->store_result();
			$stmt->bind_result($message_id);
			while($stmt->fetch()){
				$temp2=array('response'=>'success','message_id'=>$message_id);
			}
			echo json_encode($temp2);
		}else{
			$temp=array('response'=>'execute failed');
		echo json_encode($temp);
		}
	}else{
		$temp=array('response'=>'prepare failed');
		echo json_encode($temp);
	}
}

?>