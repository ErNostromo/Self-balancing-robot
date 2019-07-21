import sys
import subprocess
try:
    import serial
except ImportError:
    print("Serial module not available. Aborting.")
    exit(1)

END_CH = ";"
LEN_WORDS = 15


def get_arduino():
    return arduino


def get_bluetooth():
    return bluetooth


def get_string_bl():
    token = ""
    string = ""
    ch = ''
    # print(bl.inWaiting())
    while (ch != END_CH):
        ch = bluetooth.read().decode()
        if (ch == '\n'):
            continue
        token += ch
        if (ch == ' '):
            while(len(token) < LEN_WORDS):
                token += " "
            string += token
            token = ""

    return string


def setup_bl(enable_serial=False):
    global bluetooth
    global arduino
    if sys.platform == 'linux':
        if enable_serial:
            try:
                arduino = serial.Serial('/dev/ttyACM0', 115200)
                print("ACM0")
            except:
                try:
                    arduino = serial.Serial('/dev/ttyACM1', 115200)
                    print("ACM1")
                except:
                    pass
        try:
            # arduino = s('/dev/ACM0', 115200)
            bluetooth = serial.Serial('/dev/rfcomm0', 115200)
        except serial.serialutil.SerialException:
            try:
                    # arduino = s('/dev/ACM0', 115200)
                bluetooth = serial.Serial('/dev/rfcomm1', 115200)
            except serial.serialutil.SerialException:
                print(
                    "Apertura non riuscita. Eseguire 'sudo rfcomm bind rfcomm0 00:21:13:03:D0:7A' e riprovare? (y/N): ", end="")
                ans = input()
                if ans == "y" or ans == "Y":
                    try:
                        res=subprocess.call(
                            ["sudo", "rfcomm", "bind", "rfcomm0", "00:21:13:03:D0:7A"])
                        print("res: " + str(0))
                    except Exception as e:
                        print("Eccezione non gestita (3):")
                        print(e)
                        exit(1)
                    setup_bl()

                exit(1)
            except:
                print("Eccezione non gestita (2)")
                exit(1)
        except:
            print("Eccezione non gestita (1)")
            exit(1)

    elif sys.platform == 'win32':
        print("Ancora non implementato per win")
        exit(1)
