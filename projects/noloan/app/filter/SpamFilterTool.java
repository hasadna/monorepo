package projects.noloan.app.filter;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import com.google.startupos.common.CommonModule;
import com.google.startupos.common.FileUtils;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;
import com.google.startupos.common.flags.Flags;
import dagger.Component;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import projects.noloan.app.Protos.SmsMessage;
import projects.noloan.app.Protos.SmsMessageList;

/** A tool for running the SMS Spam filter. */
@Singleton
public class SpamFilterTool {
  private static final FluentLogger log = FluentLogger.forEnclosingClass();
  FileUtils fileUtils;

  public static enum CsvColumns {
    SENDER,
    CONTENTS,
  }

  @FlagDesc(name = "messages_prototxt", description = "Prototxt of messages to filter")
  public static final Flag<String> messagesPrototxt = Flag.create("");

  @FlagDesc(name = "messages_csv", description = "CSV of messages to filter")
  public static final Flag<String> messagesCsv = Flag.create("");

  @FlagDesc(name = "output_prototxt", description = "Prototxt of messages detected as spam")
  public static final Flag<String> outputPrototxt = Flag.create("");

  @Inject
  SpamFilterTool(FileUtils fileUtils) {
    this.fileUtils = fileUtils;
  }

  private static void checkFlags() {
    if (messagesPrototxt.get().isEmpty() && messagesCsv.get().isEmpty()) {
      System.out.println("Error: Please set --messages_prototxt or --messages_csv");
      System.exit(1);
    }
    if (!messagesPrototxt.get().isEmpty() && !messagesCsv.get().isEmpty()) {
      System.out.println("Error: Please set only one input file in flags");
      System.exit(1);
    }
    if (outputPrototxt.get().isEmpty()) {
      System.out.println("Error: --output_prototxt must be set");
      System.exit(1);
    }
  }

  private SmsMessageList getMessagesFromCsv() {
    SmsMessageList.Builder result = SmsMessageList.newBuilder();

    try {
      Reader fileReader = new FileReader(fileUtils.expandHomeDirectory(messagesCsv.get()));
      List<CSVRecord> records =
          CSVFormat.DEFAULT
              .withDelimiter('\t')
              .withSkipHeaderRecord()
              .withHeader(CsvColumns.class)
              .parse(fileReader)
              .getRecords();

      for (CSVRecord record : records) {
        String sender = record.get(CsvColumns.SENDER);
        String contents = record.get(CsvColumns.CONTENTS);
        SmsMessage message =
            SmsMessage.newBuilder().setSender(sender).setContents(contents).build();
        result.addMessage(message);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return result.build();
  }

  /* Returns messages to filter from prototxt or csv */
  private ImmutableList<SmsMessage> getMessagesToFilter() {
    SmsMessageList messagesToFilter;
    if (!messagesPrototxt.get().isEmpty()) {
      messagesToFilter =
          (SmsMessageList)
              fileUtils.readPrototxtUnchecked(messagesPrototxt.get(), SmsMessageList.newBuilder());
    } else {
      messagesToFilter = getMessagesFromCsv();
    }
    return ImmutableList.copyOf(messagesToFilter.getMessageList());
  }

  @Singleton
  @Component(modules = {CommonModule.class})
  public interface SpamFilterToolComponent {
    SpamFilterTool getSpamFilterTool();
  }

  private void run() {
    ImmutableList<SmsMessage> messagesToFilter = getMessagesToFilter();
    SpamFilter spamFilter = new SpamFilter(messagesToFilter);
    ImmutableList<SmsMessage> messagesDetectedAsSpam = spamFilter.filter(messagesToFilter);
    fileUtils.writePrototxtUnchecked(
        SmsMessageList.newBuilder().addAllMessage(messagesDetectedAsSpam).build(),
        outputPrototxt.get());
  }

  public static void main(String[] args) throws Exception {
    Flags.parse(args, SpamFilterTool.class.getPackage());
    checkFlags();
    DaggerSpamFilterTool_SpamFilterToolComponent.create().getSpamFilterTool().run();
  }
}

