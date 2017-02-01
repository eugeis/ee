package ee.axon.bank.core

class CreateAccountCommand(val accountId: String, val overdraftLimit: Int)
class WithdrawMoney(val accountId: String, val amount: Int)

class AccountCreatedEvent(val accountId: String, val overdraftLimit: Int)
class MoneyWithdrawEvent(val accountId: String, val amount: Int, val balance: Int)