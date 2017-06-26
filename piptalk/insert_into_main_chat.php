<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();

}
else
{

	$message_body = $_POST["message_body"];
	$language_code = $_POST["language_code"];
	$time = $_POST["time"];
 	$date = $_POST["date"];
	$sender_id = $_POST["sender_id"];
	$receiver_id = $_POST["receiver_id"];
	$m_sent = $_POST["sent"];
    $m_received = $_POST["received"];
 

	 $stmt = $Con->prepare("INSERT INTO main_chat (message_body,msg_lang_code, time, date, sender_id, receiver_id, m_sent, m_received) VALUES (?,?,?,?,?,?,?,?) ;" );
	 
          $stmt->bind_param('ssssiiss', $message_body, $language_code, $time ,$date, $sender_id ,$receiver_id, $m_sent , $m_received );
	
		if($stmt->execute())
		{
				$query="SELECT message_id from main_chat WHERE (date='$date') AND (time='$time') AND ((sender_id='$sender_id' AND receiver_id='$receiver_id') OR(sender_id='$receiver_id' AND receiver_id='$sender_id') ) ;";
				if($stmt=$Con->prepare($query)){
					if($stmt->execute()){
						$stmt->store_result();
						$count=$stmt->num_rows();
						if($count==1){
							$stmt->bind_result($message_id);
							$stmt->fetch();
							$userinfo=array('response'=>'Done','message_id'=>$message_id);
				            echo json_encode($userinfo);
						}else{
							echo 'count not equal 1';
						}
					}else{
						echo 'execute failed for query 2';
					}
				}else{
					echo 'failed prepare query 2';
				}

			}
			else
			{
				$userinfo['response']="error in query execution";
				echo json_encode($userinfo);
			}
		}
	
	
?>