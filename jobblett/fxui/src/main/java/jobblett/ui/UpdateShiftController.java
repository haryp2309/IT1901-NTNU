package jobblett.ui;

import static jobblett.ui.JobblettScenes.SHIFT_VIEW;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import jobblett.core.JobShift;
import jobblett.core.User;

public class UpdateShiftController extends SceneController {

  @FXML ListView<User> members;

  @FXML DatePicker date;

  @FXML TextField fromField;

  @FXML TextField toField;

  @FXML TextArea infoArea;

  @FXML Button goBackButton;

  @FXML Button createShiftButton;

  @FXML Label errorMessage;

  private JobShift activeJobShift;

  /**
   * TODO.
   */
  @FXML public void initialize() {
    // setting up dateformat for the datepicker
    date.setConverter(new StringConverter<LocalDate>() {

      @Override public String toString(LocalDate date) {
        if (date == null) {
          return "";
        }

        return App.EXPECTED_DATE_FORMAT.format(date);
      }

      @Override public LocalDate fromString(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
          return null;
        }

        return LocalDate.parse(dateString, App.EXPECTED_DATE_FORMAT);
      }
    });

    // making it not able to write in date, the user has to use to calender to pick
    // a date
    date.setEditable(false);
    date.setDayCellFactory(new DatePickerDayCell());

    //Det her er litt mye logikk, burde kanskje heller ligge i en core klasse?
    ChangeListener<String> listener = (observable, oldValue, newValue) -> {

      /*
       * String pattern = "^(?=.*[0-9])(?=.*[:]).{0,}$";
       * if(!newValue.matches(pattern)){ if(oldValue.matches(pattern))
       * fromField.setText(oldValue); else fromField.setText("00:00"); }
       */

      if (newValue.length() > 5) {
        errorMessage.setText("Time period is not written in the correct format");
        return;
      }

      //Finnes definitivt bedre måter å gjøre dette på
      List<String> patterns = List.of("[0-2]", "[0-9]", ":", "[0-5]", "[0-9]");

      for (int i = 0; i < newValue.length(); i++) {
        if (!String.valueOf(newValue.charAt(i)).matches(patterns.get(i))) {
          errorMessage.setText("Time period is not written in the correct format");
          return;
        }
      }
      errorMessage.setText("");
    };
    //utbedre til å detektere feil inntast automatisk??
    fromField.textProperty().addListener(listener);
    toField.textProperty().addListener(listener);
  }

  //TODO: trenger kanskje ikke fet skrift for admin her?
  @Override public void onSceneDisplayed() {

    // Lists all members
    members.setCellFactory(member -> new GroupMemberListCell(getControllerMap()));
    members.getItems().clear();
    for (User user : getActiveGroup()) {
      members.getItems().add(user);
    }
    errorMessage.setText("");

    if (activeJobShift == null) {
      // Create new JobShift
      fromField.setText("12:00");
      toField.setText(("19:30"));
      date.setValue(LocalDate.now());
      infoArea.setText("");
      createShiftButton.setText("Create shift");
    } else {
      // Update existing JobShift
      if (activeJobShift.getUser() != null) {
        members.getSelectionModel().select(activeJobShift.getUser());
      }
      String fromTime = activeJobShift.getStartingTime().format(App.EXPECTED_TIME_FORMAT);
      String toTime = activeJobShift.getEndingTime().format(App.EXPECTED_TIME_FORMAT);
      date.setValue(activeJobShift.getStartingTime().toLocalDate());
      fromField.setText(fromTime);
      toField.setText(toTime);
      infoArea.setText(activeJobShift.getInfo());
      createShiftButton.setText("Update shift");
    }
  }

  @FXML public void goBack() throws IOException {
    activeJobShift = null;
    switchScene(SHIFT_VIEW);
  }


  /**
   * TODO.
   */
  @FXML public void createShift() {
    try {
      User user = members.getSelectionModel().getSelectedItem();
      String info = infoArea.getText();
      LocalDateTime startingTime = getStartingTime(date.getValue(), fromField.getText());
      Duration duration = getDuration(fromField.getText(), toField.getText());
      JobShift newShift = new JobShift(user, startingTime, duration, info);
      
      if (newShift.isOutDated()) {
        throw new IllegalArgumentException("Starting time must be later than the current time");
      }
      //TODO: Litt rart at vi har en direkte addJobShift metode, 
      //men at får å fjerne et job skift så må man først
      //bruke getJobShiftList.
      getActiveGroup().addJobShift(newShift, getActiveUser());
      if (activeJobShift != null) {
        getActiveGroup().getJobShiftList().remove(activeJobShift);
      }
      goBack();
    } catch (Exception e) {
      errorMessage.setText(e.getMessage());
    }
  }

  private LocalDateTime getStartingTime(LocalDate date, String timeString) {
    return LocalDateTime.of(date, stringToTime(timeString));
  }

  private Duration getDuration(String from, String to) {
    return Duration.between(stringToTime(from), stringToTime(to));
  }

  private LocalTime stringToTime(String timeString) {
    try {
      int splitIndex = timeString.indexOf(":");
      int hour = Integer.parseInt(timeString.substring(0, splitIndex));
      int minute = Integer.parseInt(timeString.substring(splitIndex + 1));
      return LocalTime.of(hour, minute);
    } catch (Exception e) {
      throw new IllegalArgumentException("Time period is not written in the correct format");
    }
  }

  @Override public void styleIt() {
    super.styleIt();
    goBackButton.setSkin(new ButtonAnimationSkin(goBackButton));
    createShiftButton.setSkin(new ButtonAnimationSkin(createShiftButton));
  }

  protected void setActiveJobShift(JobShift activeJobShift) {
    this.activeJobShift = activeJobShift;
  }
}
