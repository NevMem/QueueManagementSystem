from tools.simple_scenarios import *
from tools.scenario import scenarios
from tools.exception_with_body import *
from tools.reporter import Reporter

def run_scenarios():

    reporter = Reporter('run_results.json')

    for scenario in scenarios:
        name, func = scenario['scenario_name'], scenario['scenario']
        print('Running scenario', name)

        try:
            func()
            reporter.report_success(name)
        except ExceptionWithBody as err:
            reporter.report_fail(name, err, is_flaky=scenario['flaky'])
        except Exception as ex:
            print(ex)
            reporter.report_fail(name, ExceptionWithBody(str(ex), 0), is_flaky=scenario['flaky'])

    reporter.write_report()


if __name__ == '__main__':
    run_scenarios()
