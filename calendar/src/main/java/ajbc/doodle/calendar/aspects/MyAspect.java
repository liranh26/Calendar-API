package ajbc.doodle.calendar.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;



@Aspect
@Component
public class MyAspect {
	@Around("execution(* ajbc.doodle.calendar.daos.ProductDao.get*(Double, Double))")
	public Object swapInputs(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object[] args = proceedingJoinPoint.getArgs();
		Double min = (Double) args[0];
		Double max = (Double) args[1];
		if(max<min) {
			args = new Object[] {max, min};
		}
		return proceedingJoinPoint.proceed(args);
	}
	
	
	@AfterThrowing(throwing = "ex", pointcut = "execution(* ajbc.doodle.calendar.daos.ProductDao.*(..))")
	public void convertToDaoException(Throwable ex) throws DaoException {
		throw new DaoException(ex);
	}
}
