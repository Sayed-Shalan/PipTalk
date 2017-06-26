<?php 
$Con=mysqli_connect("localhost","root","","chat");
if(mysqli_connect_errno())
{
	echo"failed to connect".mysqli_connect_error();
	
}

else
{
	$user_id = $_POST["user_id"];

	$query="SELECT native_language,gender,has_image,status,phone_number,date_of_birth FROM user_info WHERE user_id='$user_id' ;";
	  
		if($stmt=$Con->prepare($query))
		{
		if($stmt->execute())
		{
				$stmt->store_result();
			$count = $stmt->num_rows();
		
			if($count==1)
			{
				$stmt->bind_result($native_language,$gender,$has_image,$status,$phone_number,$date_of_birth);

				while($stmt->fetch())
				{
					$temp = array ('native_language'=>$native_language,'gender'=>$gender,'has_image'=>$has_image,'status'=>$status,'phone_number'=>$phone_number,'date_of_birth'=>$date_of_birth,);
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