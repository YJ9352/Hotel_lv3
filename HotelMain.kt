package hotel.lv2

import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.system.exitProcess

// 이게 체크인 체크아웃 날짜 담는건가??
fun withInDate(stx: LocalDate, etx: LocalDate, check: LocalDate): Boolean {
    return !check.isBefore(stx) && check.isBefore(etx)
}

@Suppress("UNREACHABLE_CODE")
class HotelMain {
    fun hotelMenu(infoMenu: Int) {
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val rsvHistory = arrayListOf<RsvHistory>()
        val customers = arrayListOf<Customer>()

        when (infoMenu) {
            1 -> {
                println("${infoMenu}번을 선택하셨습니다")
                println("호텔 예약을 시작합니다.")

                val randomMoney = (50000..1000000).random() // 랜덤 고객 잔액 설정

                println("예약자 성함을 입력해주세요.")
                val name = readLine() // 예약자 이름 입력
                var roomNum: Int // 방 번호

                // 방 번호 지정
                while (true) {
                    println("예약하실 방 번호를 입력해주세요. (100 ~ 999)")
                    var emptyRoom = readLine() // 방 예약번호 입력

                    if (emptyRoom != null) {
                        if (!emptyRoom.isNumeric()) {
                            System.err.println("방 번호 입력은 숫자만 해주세요.")
                            continue
                        }

                        if (emptyRoom.toInt() in 100 .. 999) {
                            roomNum = emptyRoom.toInt() // 범위 맞게 입력시 Int로 바꿔서 저장
                            println("선택하신 방 번호는 ${roomNum} 입니다.")
                            break
                        } else {
                            System.err.println("예약 가능한 방 번호가 아닙니다. 다시 입력해주세요. (100 ~ 999)")
                            continue
                        }
                    }
                }

                // 체크인 날짜 지정 , 체크인 날짜를 맞게 입력하면 자꾸 여기로 돌아와서 반복됨 왜?
                var checkIn: LocalDate? = null
                while (true) {
                    println("오늘 날짜는 ${LocalDate.now()} 입니다. 체크인 날짜를 입력해주세요.(yyyyMMdd)")
                    var checkDate = readLine() // 체크인 날짜 입력

                    try {
                        val temp = LocalDate.from(dateFormat.parse(checkDate))
                        if (temp.isBefore(LocalDate.now())) {
                            System.err.println("예약이 불가능합니다.")
                            println("오늘 : ${LocalDate.now()} 또는 이후 날짜를 입력해주세요.")
                            continue
                        }
                        val existRoom = rsvHistory.filter { it.resvRoom == roomNum }
                        if (existRoom.isNotEmpty()) {
                            for (emptyRoom in existRoom) {
                                if (!withInDate(emptyRoom.checkIn, emptyRoom.checkOut, temp)) {
                                    checkIn = temp
                                } else {
                                    System.err.println("해당 날짜는 이미 예약된 방입니다.")
                                    println("다른 방을 선택해주세요.")
                                    break
                                }
                            }

                        } else {
                            checkIn = temp
                        }

                        if (checkIn == null) {
                            continue
                        }

                    } catch (e: DateTimeException) {
                        System.err.println("올바르지 않은 입력입니다.")
                        println("날짜 형식을 주의하여 다시 입력해 주세요. (yyyyMMdd)")
                        continue
                    }
                }

                // 체크아웃 날짜 지정
                var checkOut: LocalDate? = null
                while (true) {
                    println("예약 날짜는 ${checkIn} 입니다.")
                    println("체크아웃 날짜를 입력해 주세요. (yyyyMMdd)")
                    var checkDate = readLine() // 체크아웃 날짜 입력

                    try {
                        val temp = LocalDate.from(dateFormat.parse(checkDate))
                        if (temp.isBefore(checkIn) || temp.isEqual(checkIn)) {
                            System.err.println("체크인 날짜와 같거나 이전입니다. 다시 입력해 주세요.")
                            continue
                        }

                        val existRoom = rsvHistory.filter { it.resvRoom == roomNum }
                        if (existRoom.isNotEmpty()) {
                            for (emptyRoom in existRoom) {
                                if (!withInDate(emptyRoom.checkIn, emptyRoom.checkOut, temp)) {
                                    checkOut = temp
                                } else {
                                    println("해당 날짜는 방을 사용중입니다. 다른 날짜를 입력해주세요.")
                                    break
                                }
                            }
                        } else {
                            checkOut = temp
                        }

                        if (checkOut == null) {
                            continue
                        } else {
                            break
                        }

                    } catch (e: DateTimeParseException) {
                        System.err.println("올바르지 않은 입력입니다.")
                        println("날짜 형식을 주의하여 다시 입력해 주세요. (yyyyMMdd)")
                        continue
                    }
                }

                // 명단 추가
                var customer = customers.find { it.name == name }
                if (customer == null) {
                    customer = Customer(name = name.toString())
                    customers.add(customer)
                }

                if (customer.money.outBalance(randomMoney, "reserve")) {
                    RsvHistory (
                        guest = customer,
                        resvMoney = randomMoney,
                        checkIn = checkIn!!,
                        checkOut = checkOut!!,
                        resvRoom = roomNum
                    ).run {
                        rsvHistory.add(this)
                    }

                    println("호텔 예약이 완료되었습니다. 감사합니다.")
                } else {
                    System.err.println("통장 잔액이 부족합니다. 예약에 실패했습니다.")
                }
            }

            2 -> {
                println("========================== 호텔 예약자 명단입니다.==========================")

            }

            3 -> {

            }

            4 -> {
                println("${infoMenu}번을 선택하셨습니다")
                println("정말로 호텔 예약을 종료합니까? 1. 종료 / 2. 처음으로")
                var endMenu = readLine()!!.toInt()
                while (true) {
                    if (endMenu.equals(1)) {
                        System.err.println("호텔 예약을 종료합니다.")
                        break // 종료가 된건지 안된건지 모르겠음
                    } else if (endMenu.equals(2)) {
                        return main()
                    }
                }
            }
        }
    }
}