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
        self.successes = []
        self.fails = []

    def report_success(self, name):
        self.successes.append(name)

    def report_fail(self, name, exception):
        self.fails.append(name)
        self.reports.append(Report(name, exception))

    def write_report(self):
        with open(self.output_filename, 'w') as out:
            out.write(json.dumps(
                {
                    'reports': self.reports,
                    'fails': self.fails,
                    'success': self.successes
                },
                cls=CustomEncoder,
                indent=4))

        for report in self.reports:
            with open('report_' + report.name + '.txt', 'w') as out:
                out.write(report.exception.response_body)
