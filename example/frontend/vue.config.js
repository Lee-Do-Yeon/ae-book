module.exports = {
	devServer: {
		port: 3000,
		proxy: {
			'/book/*': { // 내용이 가능
				target: 'http://localhost:8080'
			}
		}
	}
}