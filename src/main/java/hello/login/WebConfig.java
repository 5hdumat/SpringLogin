package hello.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.intersepter.LogInCheckInterceptor;
import hello.login.web.intersepter.LogInterseptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

/**
 * 필터를 등록하는 방법은 여러가지가 있지만, 스프링 부트를 사용한다면 FilterRegistrationBean 을 사용해서 등록하면 된다.
 * setFilter(new LogFilter()) : 등록할 필터를 지정한다.
 * setOrder(1) : 필터는 체인으로 동작한다. 따라서 순서가 필요하다. 낮을 수록 먼저 동작한다.
 * addUrlPatterns("/*") : 필터를 적용할 URL 패턴을 지정한다. 한번에 여러 패턴을 지정할 수 있다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private LogInterseptor logInterseptor;
    private LogInCheckInterceptor logInCheckInterceptor;
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    public WebConfig(LogInterseptor logInterseptor, LogInCheckInterceptor logInCheckInterceptor, LoginMemberArgumentResolver loginMemberArgumentResolver) {
        this.logInterseptor = logInterseptor;
        this.logInCheckInterceptor = logInCheckInterceptor;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
    }

    /**
     * 직접 만든 아규먼트 리졸버 등록
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    /**
     * 필터와 비교해보면 인터셉터는 addPathPatterns , excludePathPatterns 로 매우 정밀하게 URL 패턴을 지정할 수 있다.
     * 인터셉터와 필터가 중복되지 않도록 필터를 등록하기 위한 logFilter() , loginCheckFilter() 의 @Bean 은 주석처리하자.
     * 인터셉터를 적용하거나 하지 않을 부분은 addPathPatterns 와 excludePathPatterns 에 작성하면 된다.
     * 기본적으로 모든 경로에 해당 인터셉터를 적용하되 ( /** ), 홈( / ),
     * 회원가입( /members/add ), 로그인( /login ), 리소스 조회( /css/** ), 오류( /error )와 같은 부분은 로그인 체크 인터셉터를
     * 적용하지 않는다. 서블릿 필터와 비교해보면 매우 편리한 것을 알 수 있다.
     */
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterseptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/*", "/*.ico", "/error");

        // 인터셉터는 등록 과정에서 적용 경로를 세밀하게 지정할 수 있다.
        registry.addInterceptor(logInCheckInterceptor)
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/add", "/login", "/logout",
                        "/css/**", "/*.ico", "/error");
    }

    //    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();

        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");

        return filterFilterRegistrationBean;
    }

    //    @Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();

        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());

        // 순서를 2번으로 잡았다. 로그 필터 다음에 로그인 필터가 적용된다.
        filterFilterRegistrationBean.setOrder(2);

        // 일단 모든 요청에 필터를 적용하고, LoginCheckFilter 컨트롤러의 whitelist로 따로 관리
        filterFilterRegistrationBean.addUrlPatterns("/*");

        return filterFilterRegistrationBean;
    }
}
