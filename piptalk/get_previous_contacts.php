<?php
$Con=mysqli_connect("localhost","root","","chat");


if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
}
else{
	
	$user_id = $_POST['id'];
	
	$query=" SELECT * FROM main_chat WHERE sender_id = '$user_id'  OR  receiver_id = '$user_id' ORDER BY message_id DESC; ";
	
	if($stmt1=$Con->prepare($query)){
		if($stmt1->execute()){
			$data=array();
           $stmt1->store_result();
			$stmt1->bind_result($message_id,$sender_id,$receiver_id,$message_body,$msg_lang_code,$time,$date,$m_sent,$m_received);
			while($stmt1->fetch()){
				$query2="SELECT user_name ,user_token FROM users WHERE id=$sender_id";
				if($stmt=$Con->prepare($query2)){
					if($stmt->execute()){
						$stmt->store_result();
			            $count = $stmt->num_rows();
						if($count==1){
							$stmt->bind_result($sender_name,$sender_token);
			                $stmt->fetch();
							$query3="SELECT has_image FROM user_info WHERE user_id=$sender_id";
							if($stmt=$Con->prepare($query3)){
								if($stmt->execute()){
									$stmt->store_result();
			                        $count = $stmt->num_rows();
									if($count==1){
										$stmt->bind_result($sender_has_image);
			                            $stmt->fetch();
										$query4="SELECT user_name,user_token FROM users WHERE id=$receiver_id";
										if($stmt=$Con->prepare($query4)){
											if($stmt->execute()){
												$stmt->store_result();
			                                    $count = $stmt->num_rows();
												if($count==1){
													$stmt->bind_result($receiver_name,$receiver_token);
			                                        $stmt->fetch();
													$query5="SELECT has_image FROM user_info WHERE user_id='$receiver_id'";
													if($stmt=$Con->prepare($query5)){
														if($stmt->execute()){
															$stmt->store_result();
			                                                $count = $stmt->num_rows();
															if($count==1){
																$stmt->bind_result($receiver_has_image);
			                                                    $stmt->fetch();
																$temp=array('message_id'=>$message_id,
				                                                'message_body'=>$message_body,
																'msg_lang_code'=>$msg_lang_code,
																'time'=>$time,
																'date'=>$date,
																'sender_id'=>$sender_id,
																'receiver_id'=>$receiver_id,
																'sender_has_image'=>$sender_has_image,
																'receiver_has_image'=>$receiver_has_image,
																'm_sent'=>$m_sent,
																'm_received'=>$m_received,
																'sender_name'=>$sender_name,
																'receiver_name'=>$receiver_name,
																'sender_token'=>$sender_token,
																'receiver_token'=>$receiver_token);
				                                                 array_push($data,$temp);
															}
														}
													}
												}
											}
										}
										
										
									}
								}
							}
						}
					}
				}
				
				
			}
			$temp2=array('response'=>'success','user_messages'=>$data);
			echo json_encode($temp2);
		}else{
			echo "execute failed";
		}
	}else{
		echo "Error in prepare";
	}
	
}

?>