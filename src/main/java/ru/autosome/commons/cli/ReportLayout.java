package ru.autosome.commons.cli;

import ru.autosome.commons.backgroundModel.GeneralizedBackgroundModel;
import ru.autosome.commons.support.StringExtensions;

import java.util.ArrayList;
import java.util.List;

public class ReportLayout<ResultInfo> {
  public List<ValueWithDescription> parameters;
  public List<ValueWithDescription> resulting_values;
  public List<TabularParameterConfig<ResultInfo>> columns;

  public ReportLayout() {
    parameters = new ArrayList<>();
    resulting_values = new ArrayList<>();
    columns = new ArrayList<>();
  }

  public void add_parameter(String param_name, String description, Object value) {
    parameters.add(new ValueWithDescription(param_name, description, value));
  }

  public void background_parameter(String param_name, String description, GeneralizedBackgroundModel background) {
    if (!background.is_wordwise()) {
      add_parameter(param_name, description, background.toString());
    }
  }

  public void add_table_parameter(String param_name, String description, java.util.function.Function<ResultInfo, Object> callback) {
    columns.add(new TabularParameterConfig<>(param_name, description, callback));
  }

  public void add_table_parameter(String param_name, java.util.function.Function<ResultInfo, Object> callback) {
    columns.add(new TabularParameterConfig<>(param_name, null, callback));
  }

  public void add_resulting_value(String param_name, String description, Object value) {
    resulting_values.add(new ValueWithDescription(param_name, description, value));
  }
}