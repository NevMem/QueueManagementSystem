import json


class CustomEncoder(json.JSONEncoder):
    def default(self, obj):
        return obj.__dict__


class Report:
    def __init__(self, name, exception):
        self.name = name
        self.exception = exception


class Reporter:
    def __init__(self, output_filename):
        self.output_filename = output_filename
        self.reports = []

    def report(self, name, exception):
        self.reports.append(Report(name, exception))

    def write_report(self):
        with open(self.output_filename, 'w') as out:
            out.write(json.dumps(self.reports, cls=CustomEncoder))

        for report in self.reports:
            with open('report_' + report.name + '.txt', 'w') as out:
                out.write(report.exception.response_body)
