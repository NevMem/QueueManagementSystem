import json


class CustomEncoder(json.JSONEncoder):
    def default(self, obj):
        return obj.__dict__


class Report:
    def __init__(self, name, exception, is_flaky):
        self.name = name
        self.exception = exception
        self.is_flaky = is_flaky


class Reporter:
    def __init__(self, output_filename):
        self.output_filename = output_filename
        self.reports = []
        self.successes = []
        self.fails = []

    def report_success(self, name):
        self.successes.append(name)

    def report_fail(self, name, exception, is_flaky=False):
        self.fails.append(name)
        self.reports.append(Report(name, exception, is_flaky))

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
