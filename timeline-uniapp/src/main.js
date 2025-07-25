import {
	createSSRApp
} from "vue";
import App from "./App.vue";

// 引入uview-plus
import uviewPlus from 'uview-plus'

export function createApp() {
	const app = createSSRApp(App);
	
	// 使用uview-plus
	app.use(uviewPlus)
	
	// 配置uview-plus全局样式
	uni.$u.config.unit = 'rpx'
	
	return {
		app,
	};
}
