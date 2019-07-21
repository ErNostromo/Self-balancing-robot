import sys
import subprocess
import arduino_bt_library

use_xbox = True
try:
    import xbox
except ImportError:
    use_xbox = False

if len(sys.argv) > 0:
    for arg in sys.argv:
        if arg == "--no-xbox":
            use_xbox = False


def main():
    arduino_bt_library.setup_bl(False)
    bluetooth = arduino_bt_library.get_bluetooth()
    #arduino = arduino_bt_library.get_arduino()
    if use_xbox:
        controller = xbox.Joystick()
        prev_l_x = 0
        prev_l_y = 0
        prev_r_x = 0
    print_cols()

    while(1):
        try:
            string = arduino_bt_library.get_string_bl()
            print("\r" + string, end="")
            to_send = ""
            send = False
            if use_xbox:
                l_x = round(controller.leftX()*4 + 4)
                l_y = round(controller.leftY()*4 + 4)
                r_x = round(controller.rightX()*-90 + 90)
                if l_x != prev_l_x:
                    send = True
                    prev_l_x = l_x

                if l_y != prev_l_y:
                    send = True
                    prev_l_y = l_y

                if True:
                    to_send = "v" + str(l_y) + str(l_x) + ";"
                    to_send += "c" + str(r_x) + ";"
                    #print("\r" + to_send, end="")
                    bluetooth.write(to_send.encode())

                # if to_send != "":
                if False:
                    s = "v" + str(l_x) + str(l_y) + ";"
                    print(s)
                    bluetooth.write(s.encode())
                    pass

                to_send = ""
                # if (string != ''):
                #    print(string)
                #    print("-")

        except KeyboardInterrupt:
            break

    print("\nUscita")
    bluetooth.close()
    if use_xbox:
        controller.close()

    try:
        arduino.close()
    except:
        pass
    return


def print_cols():
    cols = ['angle_acc', 'angle_gyro', 'pid_error',
            'setpoint', 'output', 'left', 'right']
    for col in cols:
        while(len(col) < arduino_bt_library.LEN_WORDS):
            col += " "
        print(col, end="")
    print()


if(__name__ == '__main__'):
    main()
