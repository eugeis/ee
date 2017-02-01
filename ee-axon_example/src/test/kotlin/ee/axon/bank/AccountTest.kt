package ee.axon.bank

import ee.axon.bank.account.Account
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before

class AccountTest {
    private var fixture: SagaTestFixture<Account> = SagaTestFixture(Account::class.java)

    @Before
    fun setUp() {
        fixture = SagaTestFixture(Account::class.java)
    }

    fun testCreateAccount() {
        fixture.givenNoPriorActivity()
    }
}
