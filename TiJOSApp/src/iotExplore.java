
import java.io.IOException;
import java.util.Random;

import tijos.framework.networkcenter.tencent.IotExploreEventListener;
import tijos.framework.networkcenter.tencent.IotExploreMqttClient;
import tijos.framework.platform.lte.TiLTE;
import tijos.framework.util.Delay;
import tijos.framework.util.json.JSONObject;

class IotEventListener implements IotExploreEventListener {

	IotExploreMqttClient client = null;

	public IotEventListener(IotExploreMqttClient mqttClient) {
		this.client = mqttClient;
	}

	@Override
	public void onPropertyReportReply(String clientToken, int code, String status) {

		System.out.println("onPropertyReportReply " + clientToken + " code " + code + " status " + status);
	}

	@Override
	public void onPropertyControlArrived(String clientToken, JSONObject msg) {

		System.out.println("onPropertyControlArrived " + clientToken + " " + msg);

		int code = 0;
		String status = "OK";
		try {
			client.propertyControlReply(clientToken, code, status);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void onActionArrived(String clientToken, String actionId, JSONObject params) {
		System.out.println("onActionArrived " + actionId + " " + params);

		// check reply
		JSONObject response = new JSONObject();

		int code = 0;
		String status = "OK";
		try {
			response.put("result", 1);

			client.actionResultReply(clientToken, code, status, response);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void onEventReportReply(String clientToken, int code, String status) {
		System.out.println("onEventReportReply " + clientToken + " code " + code + " status " + status);
	}

	@Override
	public void onMqttConnected() {
		System.out.println("onMqttConnected");
	}

	@Override
	public void onMqttDisconnected(int error) {
		System.out.println("onMqttDisconnected");
	}

}

/**
 * Tencent IoT Explore Demo
 * @author TiJOS
 *
 */
public class iotExplore {

	public static void main(String[] args) throws IOException {

		System.out.println("Start ...");

		TiLTE.getInstance().startup(20);

		//设备密钥信息	
		String productId = "68NRL44HNK";
		String deviceName = "device1";
		String devicePSK = "PQ7Ja8O0/j9Bbm7WdW+TuQ==";

		//腾讯云平台客户端
		IotExploreMqttClient txClient = IotExploreMqttClient.getInstance(productId, deviceName, devicePSK);
		
		//启动连接并设置事件监听
		txClient.connect(new IotEventListener(txClient));

		//通过JSON对象进行设备属性上报
		JSONObject jobj = new JSONObject();
		jobj.put("switch", 1);
		jobj.put("color", 0);
		jobj.put("brightness", 0);

		txClient.propertyReport(jobj);
		System.out.println(jobj);

		//事件上报
		JSONObject jalarm = new JSONObject();
		jalarm.put("alarm", 1);
		txClient.eventReport("alarm", IotExploreMqttClient.EVENT_TYPE_INFO, jalarm);

		//每5秒上报
		Random rand = new Random();
		while(true)
		{
			jobj.put("switch", 1);
			jobj.put("color", 0);
			jobj.put("brightness", rand.nextInt(100));
		
			txClient.propertyReport(jobj);
			System.out.println(jobj);
			
			Delay.msDelay(5000);
		}

		//txClient.disconnect();

	}

}
