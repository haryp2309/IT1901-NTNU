package jobblett.ui;

import java.time.LocalDate;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

/**
 * CellFactory for the datePicker used in UpdateShiftController.
 */
public class DatePickerDayCell implements Callback<DatePicker, DateCell> {
  /**
   * Disables past dates.
   *
   */
  @Override public DateCell call(DatePicker param) {
    return new DateCell() {
      @Override public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        if (date.isBefore(LocalDate.now())) {
          setDisable(true);
        }
      }
    };
  }


}
