package hasadna.projects.noloan.server;

import com.google.protobuf.Message;
import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.firestore.FirestoreProtoClient;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.tools.reviewer.local_server.service.AuthService;
import dagger.Component;
import hasadna.noloan.protobuf.SmsProto.SmsMessage;
import hasadna.noloan.protobuf.SmsProto.SpamList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Singleton
public class SuggestedSpamViewer {
	
	private static final String SPAM_DOCUMENT_PATH = "noloan/spam_suggestions/suggestions";
	
	private FileUtils fileUtils;
	private AuthService authService;
	
	@Inject
	SuggestedSpamViewer(FileUtils fileUtils, AuthService authService) {
		this.fileUtils = fileUtils;
		this.authService = authService;
	}
	
	void run() throws IOException {
		authService.refreshToken();
		FirestoreProtoClient client =	new FirestoreProtoClient(authService.getProjectId(), authService.getToken());
		
		List<Message> list = client.getProtoDocuments(SPAM_DOCUMENT_PATH,SmsMessage.newBuilder());

		// Creating the SpamList from the list of SMSs
		SpamList.Builder spamBuilder = SpamList.newBuilder();
		for(int i = 0; i< list.size();i++)
		{
			spamBuilder.addSms(((SmsMessage)list.get(i)));

		}
		SpamList spam = spamBuilder.build();

		//System.out.println(spam);
		//fileUtils.writePrototxt(spam,"suggested.prototxt");
		fileUtils.writePrototxt(spam,"projects/noloan/server/suggested.prototxt");
	}
	
	@Singleton
	@Component(modules = CommonModule.class)
	interface ToolComponent {
		SuggestedSpamViewer getTool();
	}
	
	public static void main(String[] args) throws IOException {
		Flags.parse(args, AuthService.class.getPackage());
		DaggerSuggestedSpamViewer_ToolComponent.create().getTool().run();
	}
}

